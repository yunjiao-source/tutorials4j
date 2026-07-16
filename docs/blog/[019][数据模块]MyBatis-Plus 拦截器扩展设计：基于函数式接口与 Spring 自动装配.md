# [019][数据模块]MyBatis-Plus 拦截器扩展设计：基于函数式接口与 Spring 自动装配

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在基于 MyBatis-Plus 的数据访问层开发中，拦截器（Interceptor）是扩展分页、乐观锁、防全表误操作等能力的核心组件。通常我们会在配置类中手动创建 `MybatisPlusInterceptor` 并添加各个 `InnerInterceptor`。这种方式虽然直观，但当需要支持模块化、按顺序注册、允许外部定制时，就显得不够灵活。

本文介绍一种更优雅的设计：**通过函数式接口 `InnerInterceptorSupplier` + Spring 的 `ObjectProvider` 机制，实现拦截器的自动收集与顺序装配**。并结合 `DataMybatisPlusConfiguration` 的示例代码，分析其设计思想与实战用法。

---

## 一、背景：MyBatis-Plus 拦截器体系

MyBatis-Plus 提供了一套基于 `Interceptor` 的插件机制，其中 `MybatisPlusInterceptor` 是责任链的核心，它可以添加多个 `InnerInterceptor`（如分页、乐观锁、防全表攻击等）。典型配置如下：

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
}
```

这种写法的缺点：
- 如果是框架或 starter 项目，用户想**追加**自定义拦截器，只能通过覆盖 `MybatisPlusInterceptor` Bean 的方式，容易丢失默认拦截器。
- 拦截器的**顺序**不易被外部控制。
- 不够“声明式”，缺乏扩展点。

---

## 二、核心接口：`InnerInterceptorSupplier`

```java
@FunctionalInterface
public interface InnerInterceptorSupplier extends Supplier<InnerInterceptor> {
}
```

这是一个极其简洁的函数式接口，继承自 `Supplier<InnerInterceptor>`。它的作用不是直接提供拦截器，而是**声明一个能够提供拦截器的工厂**。

在 Spring 容器中，任何实现了该接口的 Bean 都会被识别，并用于向 `MybatisPlusInterceptor` 贡献拦截器实例。这样做的好处：
- **延迟创建**：`Supplier` 允许在真正需要时才 `get()` 拦截器。
- **允许带参数的构造**：比如分页拦截器需要 `DbType`，可以在 Supplier 实现中从配置中读取。
- **支持 `@Order` 排序**：多个 Supplier Bean 可以定义顺序，最终拦截器链的顺序就是这些 Supplier 的执行顺序。

---

## 三、自动配置类：`DataMybatisPlusConfiguration`

### 3.1 核心 Bean：`mybatisPlusInterceptor`

```java
@Bean
MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<InnerInterceptorSupplier> innerInterceptorSuppliers) {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    innerInterceptorSuppliers.orderedStream()
            .map(InnerInterceptorSupplier::get)
            .forEach(interceptor::addInnerInterceptor);
    return interceptor;
}
```

关键点解析：
- `ObjectProvider<InnerInterceptorSupplier>`：Spring 提供的依赖注入方式，能获取指定类型的所有 Bean，并支持有序流（`orderedStream()`）。
- `orderedStream()` 会按照每个 Bean 上的 `@Order` 注解或 `Ordered` 接口进行排序。
- 依次调用 `get()` 获取拦截器实例，添加到 `MybatisPlusInterceptor` 中。

这种设计使得**拦截器的增加完全基于容器中的 Supplier Bean**，而不是硬编码。外部模块只需要定义自己的 `InnerInterceptorSupplier` Bean 即可自动生效。

### 3.2 默认注册的三个内置拦截器

配置类通过 `@Bean` 方法提供了三个预置的 Supplier，并指定了顺序：

| 顺序 | 拦截器类型 | 作用 |
|------|------------|------|
| 100 | `PaginationInnerInterceptor` | 自动分页，根据数据库方言生成分页 SQL |
| 200 | `OptimisticLockerInnerInterceptor` | 乐观锁，配合 `@Version` 字段使用 |
| 300 | `BlockAttackInnerInterceptor` | 防全表更新/删除，防止无 where 条件的 SQL |

代码示例：

```java
@Bean
@Order(100)
InnerInterceptorSupplier paginationInnerInterceptorSupplier(DataProperties properties) {
    return () -> new PaginationInnerInterceptor(properties.getMybatisPlus().getDbType());
}
```

注意分页拦截器需要从配置类 `DataProperties` 中获取数据库类型，这正是使用了 Supplier 的延迟构造能力。

---

## 四、如何扩展自定义拦截器？

假设我们需要添加一个 SQL 性能监控拦截器（自定义实现 `InnerInterceptor`），步骤如下：

1. 实现自定义拦截器（例如 `PerformanceInnerInterceptor`）。
2. 在任意 `@Configuration` 类中声明一个 `InnerInterceptorSupplier` Bean，并指定 `@Order`（决定它在拦截器链中的位置）。

```java
@Configuration
public class MyCustomInterceptorConfig {

    @Bean
    @Order(50)  // 将在分页拦截器（Order=100）之前执行
    public InnerInterceptorSupplier performanceInterceptorSupplier() {
        // 如果拦截器需要其他依赖，可以在这里从方法参数传入
        return PerformanceInnerInterceptor::new;
    }
}
```

启动 Spring Boot 应用后，`DataMybatisPlusConfiguration` 会自动收集该 Supplier 并将其拦截器添加到链中。

如果想**替换**某个默认拦截器（比如修改分页拦截器的参数），无需覆盖整个 `MybatisPlusInterceptor`，只需提供一个**更高优先级**（`@Order` 更小）的同类型 Supplier 会怎么样？实际上不会替换，而是增加。若要真正的替换，需要小心设计：最好在配置类中通过 `@ConditionalOnMissingBean` 来让默认 Supplier 有条件注册。不过当前设计下，默认 Supplier 总是注册，所以用户如果想完全自定义分页逻辑，可以提供一个相同类型的拦截器（但顺序可能需要在默认之前或之后，取决于业务需要）。更标准的做法是让默认的核心拦截器也支持条件化，但本文不展开。

---

## 五、设计优势总结

| 设计点 | 传统方式 | 本文方式 |
|--------|----------|----------|
| 扩展性 | 覆盖整个 `MybatisPlusInterceptor` Bean | 只需添加一个 `InnerInterceptorSupplier` Bean |
| 顺序控制 | 硬编码 `addInnerInterceptor` 顺序 | `@Order` 注解声明式排序 |
| 延迟创建 | 无 | Supplier 支持带参或条件化创建 |
| 框架集成友好 | 较差 | 符合 Spring 自动装配哲学，适合 Starter 开发 |

---

## 六、潜在注意点

1. **顺序一致性问题**：`orderedStream()` 依赖 `@Order`，但不同 Supplier 之间若无明确顺序，行为可能不确定。建议所有自定义 Supplier 都显式标注 `@Order`。
2. **拦截器生效范围**：`MybatisPlusInterceptor` 对 MyBatis-Plus 的所有方法都生效，但部分拦截器（如分页）只对特定执行器有效，这是 MyBatis-Plus 自身行为。
3. **重复添加风险**：若多个 Supplier 返回同一类型的拦截器（比如两个分页拦截器），可能会产生冲突。通常默认配置已足够，扩展时应注意避免重复。
4. **性能开销**：Supplier 的调用仅在容器启动且装配拦截器时执行一次，运行时无额外开销。

---

## 七、总结

`InnerInterceptorSupplier` + `DataMybatisPlusConfiguration` 的设计展现了一种**面向扩展开放，面向修改关闭**的优雅实践。它充分利用了 Java 8 的 `Supplier` 函数式接口、Spring 的 `ObjectProvider` 有序注入以及 `@Order` 排序机制，使得 MyBatis-Plus 拦截器链的构建变得高度可插拔、可排序、可配置。

如果你的项目中也需要封装 MyBatis-Plus 基础设施，或者开发一个数据访问 Starter，这种模式值得借鉴。完整的代码已在 `tutorials4j` 框架中实现，读者可根据自身需求裁剪或增强。

阅读最新文章，请关注我的微信公众号`杨运交` 