# [021][数据模块]基于`BaseEnum`的统一枚举处理方案：序列化与 JPA 转换实践

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在 Java 企业级开发中，枚举（Enum）是表达固定常量集合（如状态、类型、字典值）的常用手段。然而原生枚举在跨层传输（JSON 序列化）与持久化（ORM 存储）时存在一些不足：

- **JSON 序列化默认输出枚举名**：前端难以通过枚举的 `code` 进行逻辑判断，且枚举名重构会破坏 API 兼容性。
- **数据库存储枚举名或未定义的code**：`@Enumerated(EnumType.STRING)` 存储枚举名，重构风险高；`EnumType.ORDINAL` 基于序数，极易出错。  

针对上述痛点，本文介绍一套基于 `BaseEnum` 接口的通用解决方案，涵盖了 Jackson 序列化定制和 JPA 属性转换器，使得枚举在前端和数据库中统一通过 `code` 表示，并自动解决 `Long` 类型的序列化溢出问题。

---

## 一、核心接口：`BaseEnum<T>`

所有需要统一处理的枚举都应实现该接口：

```java
public interface BaseEnum<T> {
    T getCode();      // 枚举的编码（推荐使用 Integer / String 等可读且稳定的值）
    String getName(); // 可读名称，用于前端展示或日志
}
```

**设计意图**：将枚举的“业务标识”与“显示名称”解耦。`code` 是稳定的业务键，`name` 供前端友好展示。接口泛型 `T` 支持不同数据类型的 `code`（如 `Integer`、`String`、`Short`）。

**示例枚举**：

```java
public enum OrderStatus implements BaseEnum<Integer> {
    PENDING(0, "待处理"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    CANCELED(3, "已取消");

    private final Integer code;
    private final String name;

    OrderStatus(Integer code, String name) { this.code = code; this.name = name; }
    @Override public Integer getCode() { return code; }
    @Override public String getName() { return name; }
}
```

---

## 二、JSON 序列化定制

### 1. 自定义序列化器：`BaseEnumJsonSerializer`

默认情况下，Jackson 会将枚举序列化为枚举常量名（如 `"PENDING"`）。我们希望输出为包含 `code` 和 `name` 的 JSON 对象，方便前端同时获取编码和展示文本。

```java
public class BaseEnumJsonSerializer extends JsonSerializer<BaseEnum<?>> {
    public static final BaseEnumJsonSerializer instance = new BaseEnumJsonSerializer();

    @Override
    public void serialize(BaseEnum<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) { gen.writeNull(); return; }
        gen.writeStartObject();
        gen.writeObjectField("code", value.getCode());
        gen.writeObjectField("name", value.getName());
        gen.writeEndObject();
    }
}
```

**关键点**：
- 全局单例，减少对象创建开销。
- 覆盖 `handledType()` 返回 `BaseEnum.class`，表明该序列化器适用于所有 `BaseEnum` 子类型。
- 输出格式：`{"code":0,"name":"待处理"}`。

### 2. 注册模块：`BaseEnumSimpleModule`

Jackson 模块（`SimpleModule`）用于集中注册自定义序列化器、反序列化器。

```java
public class BaseEnumSimpleModule extends SimpleModule {
    public BaseEnumSimpleModule() {
        super(BaseEnumSimpleModule.class.getName(), JsonConsts.JSON_VERSION);
        this.addSerializer(BaseEnumJsonSerializer.instance);
    }
}
```

**常量 `JsonConsts.JSON_VERSION`**：记录了模块基于的 Jackson 版本，便于版本管理。

**使用方式**：在 Spring Boot 中，将该模块注册到 `ObjectMapper` 即可：

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new BaseEnumSimpleModule());
        return mapper;
    }
}
```

或通过 `spring.jackson.modules` 配置自动发现。

---

## 三、JPA 属性转换器：`AbstractBaseEnumAttributeConverter`

为了实现数据库中存储 `code`（而非枚举名或序数），我们借助 JPA 2.1 的 `AttributeConverter`。`AbstractBaseEnumAttributeConverter` 是一个抽象基类，子类只需指定枚举类型即可自动完成双向转换。

```java
public abstract class AbstractBaseEnumAttributeConverter<E extends Enum<E> & BaseEnum<T>, T>
        implements AttributeConverter<E, T> {
    private final Map<T, E> codeToEnumCache = new ConcurrentHashMap<>();

    protected AbstractBaseEnumAttributeConverter(Class<E> enumClass) {
        // 遍历枚举常量，建立 code -> 枚举 映射缓存
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        // 从缓存中根据 code 获取枚举，若无法识别则抛异常
    }
}
```

**核心特性**：
- **缓存加速**：`ConcurrentHashMap` 缓存所有枚举的 code 映射，避免每次都扫描 `Enum.values()`。
- **安全转换**：若数据库中出现未定义的 code，立即抛出 `DataFrameworkException`，防止数据不一致扩散。
- **泛型约束**：`E extends Enum<E> & BaseEnum<T>` 确保转换器只接收同时是枚举和 `BaseEnum` 的类型。

**实际使用**：为每个枚举类型编写一个极简的子类，并用 `@Converter` 标注：

```java
@Converter(autoApply = true)  // 自动应用于所有 OrderStatus 类型的实体属性
public class OrderStatusConverter extends AbstractBaseEnumAttributeConverter<OrderStatus, Integer> {
    public OrderStatusConverter() {
        super(OrderStatus.class);
    }
}
```

之后在实体中直接使用 `OrderStatus` 类型即可，JPA 会自动将 `code` (Integer) 写入数据库，读取时还原枚举。

---

## 四、整体工作流

1. **定义枚举**：实现 `BaseEnum<Integer>`（或其它泛型）。
2. **持久层**：创建对应的 `XxxConverter` 继承自 `AbstractBaseEnumAttributeConverter`。
3. **API 层**：注册 `BaseEnumSimpleModule` 到 `ObjectMapper`。
4. **数据库交互**：实体属性使用枚举类型，存储值即为枚举的 `code`。
5. **Controller 响应**：枚举自动序列化为 `{"code":0,"name":"待处理"}`.

---

## 五、方案优势与注意事项

### 优势

- **前后端契约稳定**：枚举的 `code` 在 API 中固定暴露，且支持新增枚举值而不破坏原有逻辑（前端只需处理未知 code 时的 fallback）。
- **数据库可读性**：存储 `code`（如 0,1,2）比存储 `ORDINAL` 更稳定，比存储 `STRING` 更节省空间且避免重命名影响。
- **统一配置**：只需一次注册 `BaseEnumSimpleModule` 和转换器，项目内所有遵循 `BaseEnum` 的枚举自动受益。=

### 注意事项

- **并发缓存安全**：`ConcurrentHashMap` 在转换器初始化时一次性填充，保证读安全。
- **空值处理**：序列化器对 `null` 输出 `null`；转换器对数据库 `null` 返回 `null` 实体属性。
- **与其它序列化器的冲突**：如果手动为某个枚举类配置了 `@JsonSerialize`，会覆盖全局的 `BaseEnumJsonSerializer`。
- **枚举重构限制**：一旦枚举的 `code` 值被用于生产数据库或 API，不应修改已有 `code`，只能新增枚举项。

---

## 六、总结

本文介绍的基于 `BaseEnum` 接口的设计，配合 Jackson 自定义模块和 JPA 抽象转换器，形成了一套完整的枚举处理闭环。它解决了原生枚举在 JSON 序列化中的信息缺失问题、在数据库映射中的脆弱性问题。在实际项目（特别是前后端分离、微服务架构）中，这套模式能显著提升代码的可维护性和接口的健壮性。

开发者可以借鉴此思路，根据自身业务调整 `BaseEnum` 结构（例如增加 `getDescription()` 方法），或扩展转换器以支持更多数据类型。最终实现“一次定义，处处统一”的枚举管理体验。


所有代码均支持泛型、线程安全，并遵循 Java Bean 规范。通过该方案，团队可以告别枚举转换的手工代码，专注于业务逻辑本身。

阅读最新文章，请关注我的微信公众号`杨运交` 