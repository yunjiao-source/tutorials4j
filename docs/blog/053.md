# [053][核心模块]Java枚举缓存与ORM集成实践

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在实际业务开发中，枚举类型常用于表示状态、类型等固定集合。为了提升代码可维护性和数据库存储的便利性，通常会将枚举与自定义编码（code）关联，并存储到数据库。然而，枚举与数据库值之间的频繁转换会带来性能开销，同时不同框架（如 MyBatis、JPA）的转换逻辑往往重复实现。本文将介绍一套通用的枚举缓存工具及与 ORM 的集成方案，实现高效、统一的枚举映射。

## 一、痛点分析

- **性能问题**：通过遍历 `Enum.values()` 查找枚举实例的时间复杂度为 O(n)，在高并发场景下可能成为瓶颈。
- **重复代码**：每个枚举都需要编写 `fromCode` 静态方法，且 MyBatis 的 `TypeHandler` 和 JPA 的 `AttributeConverter` 中逻辑相似，难以复用。
- **缓存不一致**：若多处独立实现缓存，容易导致内存浪费或数据不一致。

## 二、整体设计思路

- 提供一个全局的 **`EnumCache`** 工具类，基于 `ConcurrentHashMap` 构建两类缓存：
  - **名称缓存**：`Enum.name()` → 枚举实例  
  - **值缓存**：自定义 code（如 `Integer`、`String`） → 枚举实例  
- 定义 **`BaseEnum<T>`** 接口，统一枚举的编码获取方式。
- 为 MyBatis 和 JPA（Hibernate）分别实现转换器，内部复用 `EnumCache` 完成编解码，并支持自动注册缓存。

## 三、核心组件详解

### 1. 枚举缓存工具类 `EnumCache`

```java
public class EnumCache {
    static final Map<Class<? extends Enum<?>>, Map<Object, Enum<?>>> CACHE_BY_VALUE = ...;
    static final Map<Class<? extends Enum<?>>, Map<Object, Enum<?>>> CACHE_BY_NAME = ...;
    static final Map<Class<? extends Enum<?>>, Boolean> LOADED = ...;

    public static <E extends Enum<?>> void registerByName(Class<E> clazz, E[] es);
    public static <E extends Enum<?>> void registerByValue(Class<E> clazz, E[] es, EnumMapping<E> enumMapping);
    public static <E extends Enum<?>> E findByName(Class<E> clazz, String name, E defaultEnum);
    public static <E extends Enum<?>> E findByValue(Class<E> clazz, Object value, E defaultEnum);
}
```

**关键特性：**
- **双向缓存**：支持通过枚举名称或自定义值快速查找（O(1) 复杂度）。
- **懒加载触发**：若缓存未初始化，首次调用 `findByXxx` 会通过 `Class.forName` 强制执行枚举的静态代码块，期望静态块中完成注册。可减少显式注册的侵入性。
- **值唯一性校验**：注册基于值的缓存时，若出现重复 value 会抛出 `IllegalStateException`，避免映射歧义。
- **线程安全**：所有缓存 Map 均为 `ConcurrentHashMap`，`executeEnumStatic` 使用双重检查锁保证类加载仅一次。

**注意事项：**
- 建议在枚举类的静态块中主动调用注册方法，或通过应用启动器统一注册，避免运行时首次访问的额外开销。
- `registerByValue` 实际实现为：如果缓存已存在则仅打印 warn 日志并返回，而非注释中声称的抛出异常。该行为更温和，允许重复注册（后续注册无效）。

### 2. 基础枚举接口 `BaseEnum`

```java
public interface BaseEnum<T> {
    T getCode();      // 编码值，将存入数据库
    String getName(); // 枚举名称，通常为 name()
    String getLabel();// 可读描述
}
```

所有需要与数据库映射的业务枚举均应实现该接口，并确保 `getCode()` 返回值唯一。

### 3. MyBatis 类型处理器 `BaseEnumTypeHandler`

```java
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum<?>> extends BaseTypeHandler<E> {
    public BaseEnumTypeHandler(Class<E> type) {
        // ...
        initCache(); // 注册基于 code 的缓存
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) {
        Object code = parameter.getCode();
        // 根据 code 类型精确调用 setString/setInt/setLong/setObject
    }

    private E codeToEnum(Object code) {
        E value = EnumCache.findByValue(type, code);
        if (value == null) throw new DataFrameworkException("Unknown code: " + code);
        return value;
    }
}
```

**集成方式：**
在 MyBatis 配置文件或通过注解注册该处理器，并指定 `javaType` 为目标枚举类。由于处理器构造时会调用 `initCache` 主动注册缓存，因此无需枚举自身编写静态块。

### 4. JPA 属性转换器 `AbstractBaseEnumAttributeConverter`

```java
public abstract class AbstractBaseEnumAttributeConverter<E extends Enum<E> & BaseEnum<T>, T>
        implements AttributeConverter<E, T> {

    protected AbstractBaseEnumAttributeConverter(Class<E> enumClass) {
        // ...
        initCache(); // 同样在构造时注册缓存
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        if (dbData == null) return null;
        E enumValue = EnumCache.findByValue(enumClass, dbData);
        if (enumValue == null) throw new DataFrameworkException("无法识别的数据库值: " + dbData);
        return enumValue;
    }
}
```

**使用示例：**
```java
@Converter(autoApply = true)
public class DataStatusConverter extends AbstractBaseEnumAttributeConverter<DataStatusEnum, Integer> {
    public DataStatusConverter() {
        super(DataStatusEnum.class);
    }
}
```

通过继承该抽象类并实现无参构造即可完成 JPA 集成。`autoApply` 可根据需要配置，使所有 `DataStatusEnum` 类型的实体属性自动应用转换。

### 5. 示例枚举 `DataStatusEnum`

```java
public enum DataStatusEnum implements BaseEnum<Integer> {
    NORMAL(1, "正常"), RESERVED(2, "留存"), DISABLED(3, "禁用"),
    LOCKED(4, "锁定"), EXPIRED(5, "过期"), DELETED(6, "已删除");
    // 实现 getCode/getName/getLabel
}
```

该枚举未包含静态注册代码，但由于 MyBatis/JPA 转换器会在构造时主动调用 `EnumCache.registerByValue`，所以无需额外注册。

## 四、最佳实践建议

1. **注册时机选择**
   - 若只使用 ORM 转换器（`BaseEnumTypeHandler` 或 `AbstractBaseEnumAttributeConverter`），无需手动注册，转换器构造时自动完成。
   - 若需要在非 ORM 场景（如业务代码中根据 code 查找枚举），建议在枚举的静态块中调用 `EnumCache.registerByValue`，确保缓存始终可用：
     ```java
     static {
         EnumCache.registerByValue(DataStatusEnum.class, DataStatusEnum.values(), DataStatusEnum::getCode);
     }
     ```
   - 或者使用应用启动监听器批量注册所有 `BaseEnum` 实现类。

2. **性能优化**
   - 缓存基于 `ConcurrentHashMap`，查找效率高，适合高并发环境。
   - 枚举实例数组 `getEnumConstants()` 仅在注册时遍历一次，后续转换无额外开销。

3. **异常处理**
   - `registerByValue` 会校验值的唯一性，若枚举设计有重复 code 将快速失败，避免运行时隐藏错误。
   - `findByValue` 找不到时返回 null 或默认值，而 ORM 转换器内部会抛出明确异常，有助于排查数据脏值。

4. **类型安全**
   - `EnumCache` 内部使用原始 `Enum<?>` 和 `Object` 存储，但通过泛型方法返回时做了强制转换，调用方需确保 `Class` 类型与缓存内容匹配。
   - 建议注册和查找使用相同的枚举类对象，避免泛型擦除导致的问题。

## 五、总结

本文介绍的枚举缓存与 ORM 集成方案具有以下优点：

- **通用性**：单例缓存同时服务 MyBatis 和 JPA，避免了重复实现。
- **高性能**：哈希查找替代线性扫描。
- **低侵入**：通过 ORM 转换器自动注册缓存，业务代码无需感知；若需手动查找也仅需一行调用。
- **易扩展**：新增枚举只需实现 `BaseEnum`，并可选地添加静态注册。

该设计已在生产项目中稳定运行，显著简化了枚举映射的代码量，并提升了系统可维护性。读者可根据自身技术栈（MyBatis / JPA）选择性集成，或借鉴其缓存思想应用到其他场景。