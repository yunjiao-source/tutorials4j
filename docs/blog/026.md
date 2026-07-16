# [026][数据模块]基于 MyBatis Plus 的企业级数据访问框架设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j

## 摘要

在微服务与多租户 SaaS 架构日益普及的背景下，数据访问层需要统一解决分页、乐观锁、防 SQL 攻击、多租户隔离、审计字段自动填充等通用问题。本文介绍一套基于 MyBatis Plus 进行二次封装的轻量级框架，通过自动配置、拦截器链顺序控制、扩展点设计等机制，实现了可插拔、可配置的企业级数据访问能力。文章将详细分析其核心设计、关键组件实现以及配置方式，为类似场景下的框架封装提供参考。

## 1. 背景与目标

MyBatis Plus 作为 MyBatis 的强大增强工具，已经提供了分页、乐观锁、多租户等内置拦截器。但在实际企业应用中，仍面临以下挑战：

- **拦截器执行顺序**：不同拦截器（如租户、分页、乐观锁）有严格的顺序要求，顺序错误会导致 SQL 变形或逻辑失效。
- **可配置性**：不同模块可能需要动态开启/关闭某些拦截器（例如某些表不需要租户过滤）。
- **统一审计**：创建人、创建时间、修改人、修改时间等字段需要自动填充，避免业务代码重复。
- **扩展友好**：允许业务方自定义拦截器，并保证与框架内置拦截器协同工作。

本文分析的框架正是为了解决上述问题而设计，它以 Spring Boot AutoConfiguration 为基础，利用 `@ConditionalOnProperty` 控制拦截器开关，通过 `Ordered` 接口统一管理顺序，并提供钩子方法允许业务方注入自定义拦截器。

## 2. 整体架构

框架由以下几个核心模块组成：

- **自动配置模块**：`DataMybatisPlusConfiguration` 作为 Spring Boot 自动配置入口，按条件装配各个组件。
- **拦截器链模块**：定义 `MybatisPlusInterceptorCustomizer` 接口，各功能（分页、乐观锁、防攻击、租户）分别实现，并通过常量 `MybatisPlusConsts` 统一顺序。
- **租户模块**：`DefaultTenantLineInterceptorCustomizer` + `DefaultTenantLineHandler`，从 `TenantContextHolder` 动态获取租户 ID。
- **审计模块**：`AuditMetaObjectHandler` 实现 `MetaObjectHandler`，自动注入用户与时间信息。
- **标识符生成器**：`DefaultIdentifierGenerator` 替换 MyBatis Plus 默认的雪花算法实现。

下图展示了组件依赖关系：

```
DataMybatisPlusConfiguration
    │
    ├── 创建 MybatisPlusInterceptor（组合所有自定义器）
    ├── 条件化注册 PaginationInnerInterceptorCustomizer
    ├── 条件化注册 OptimisticLockerInterceptorCustomizer
    ├── 条件化注册 BlockAttackInterceptorCustomizer
    ├── 无条件注册 DefaultIdentifierGeneratorCustomizer
    ├── 条件化注册 AuditMetaObjectHandler
    └── （租户模块独立配置）DefaultTenantLineInterceptorCustomizer
```

## 3. 拦截器链设计：顺序与扩展

### 3.1 顺序常量定义

在 `MybatisPlusConsts` 中定义了严格的递增顺序：

```java
int INTERCEPTOR_ORDER_DEFAULT = 1;          // 预留
int INTERCEPTOR_ORDER_TENANT = 2;           // 租户先执行
int INTERCEPTOR_ORDER_PAGINATION = 3;       // 分页其次
int INTERCEPTOR_ORDER_OPTIMISTIC_LOCKER = 4;// 乐观锁
int INTERCEPTOR_ORDER_BLOCK_ATTACK = 5;     // 防攻击最后
```

> **为什么租户最先执行？**  
> 租户拦截器需要在 SQL 生成后第一时间注入租户条件，避免后续分页或乐观锁计算干扰租户字段。

### 3.2 扩展点接口

`MybatisPlusInterceptorCustomizer` 继承 `Ordered`，允许每个自定义器声明自己的顺序：

```java
@FunctionalInterface
public interface MybatisPlusInterceptorCustomizer extends Ordered {
    void custom(MybatisPlusInterceptor interceptor);
    default int getOrder() { return Ordered.LOWEST_PRECEDENCE; }
}
```

在自动配置中，所有实现了该接口的 Bean 都会被注入到 `MybatisPlusInterceptor`：

```java
@Bean
@ConditionalOnMissingBean
MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<MybatisPlusInterceptorCustomizer> customizers) {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    customizers.orderedStream().forEach(customizer -> customizer.custom(interceptor));
    return interceptor;
}
```

业务方只需实现该接口，并重写 `getOrder()` 返回合适的值，即可将自己的拦截器插入到框架链的正确位置。

### 3.3 内置自定义器实现

每个内置功能对应一个 `XxxInterceptorCustomizer`，例如分页：

```java
public class PaginationInnerInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {
    private final DbType dbType;
    @Override
    public void custom(MybatisPlusInterceptor interceptor) {
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
    }
    @Override
    public int getOrder() {
        return MybatisPlusConsts.INTERCEPTOR_ORDER_PAGINATION;
    }
}
```

乐观锁、防攻击的实现模式完全一致，均通过 `getOrder()` 返回对应常量。

## 4. 多租户隔离实现

租户模块独立于核心数据模块，通过 `DefaultTenantLineInterceptorCustomizer` 注入 `TenantLineInnerInterceptor`。其关键点：

- **租户 ID 动态获取**：`DefaultTenantLineHandler.getTenantId()` 返回 `TenantContextHolder.get()` 的值。`TenantContextHolder` 通常是基于 `ThreadLocal` 维护当前请求的租户编号，在网关或拦截器中设置。
- **忽略特定表**：通过配置 `tenant.mybatis-plus.ignore-table` 指定不需要租户过滤的表名（如系统配置表、字典表）。
- **顺序控制**：租户拦截器的顺序为 `INTERCEPTOR_ORDER_TENANT = 2`，确保它早于分页和乐观锁执行。

```java
public class DefaultTenantLineHandler implements TenantLineHandler {
    private final Set<String> ignoreTables;
    @Override
    public Expression getTenantId() {
        return new StringValue(TenantContextHolder.get());
    }
    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreTables.contains(tableName);
    }
}
```

该设计使得业务 Mapper 无需感知租户字段，所有查询自动追加 `tenant_id = ?` 条件，更新/删除也会自动带上租户条件，有效避免了跨租户数据泄露。

## 5. 审计字段自动填充

框架提供了 `AuditMetaObjectHandler` 实现 `MetaObjectHandler`，用于自动填充 `createTime`、`updateTime`、`createBy`、`updateBy` 等字段。该类通常从 `SecurityContextHolder` 或 `RequestContextHolder` 中获取当前登录用户，填充对应属性。

由于用户信息的获取方式因项目而异，框架仅提供抽象示例，业务方可继承该类或直接实现 `MetaObjectHandler` 并标记 `@Primary` 覆盖。

## 6. 标识符生成器配置

MyBatis Plus 默认使用 `DefaultIdentifierGenerator`（雪花算法），但部分老项目可能由于时钟回拨或 workerId 分配问题需要自定义。框架通过 `MybatisPlusPropertiesCustomizer` 替换了标识符生成器：

```java
@Bean
MybatisPlusPropertiesCustomizer defaultIdentifierGeneratorMybatisPlusPropertiesCustomizer() {
    return plusProperties -> plusProperties.getGlobalConfig()
            .setIdentifierGenerator(new DefaultIdentifierGenerator());
}
```

若无需替换，业务方可自行注入同类型 Bean 覆盖。

## 7. 配置与开关控制

所有拦截器均通过 `@ConditionalOnProperty` 实现开关，配置前缀为 `tutorials4j.data.mybatis-plus`（由 `PropertiesConsts` 定义），例如：

```yaml
tutorials4j:
  data:
    mybatis-plus:
      db-type: mysql                     # 数据库类型，用于分页方言
      interceptors:
        pagination: true                 # 开启分页（默认 true）
        optimistic-locker: true          # 开启乐观锁（默认 true）
        block-attack: true               # 开启防全表删除/更新（默认 true）
```

租户模块独立配置：

```yaml
tutorials4j:
  tenant:
    mybatis-plus:
      ignore-table:                      # 忽略租户过滤的表
        - sys_config
        - sys_dict
```

业务方若需要完全自定义 `MybatisPlusInterceptor`，可以直接提供该类型的 Bean，框架会由于 `@ConditionalOnMissingBean` 而跳过默认创建。

## 8. 典型使用场景与扩展示例

### 场景一：新增一个数据权限拦截器

需求：根据用户角色自动追加 `dept_id` 条件。  
实现：

```java
public class DataScopeInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {
    @Override
    public void custom(MybatisPlusInterceptor interceptor) {
        interceptor.addInnerInterceptor(new DataScopeInnerInterceptor());
    }
    @Override
    public int getOrder() {
        // 放在租户之后、分页之前
        return MybatisPlusConsts.INTERCEPTOR_ORDER_TENANT + 1;
    }
}
```

Spring 会自动发现该 Bean，并按 `orderedStream()` 顺序加入拦截器链。

### 场景二：关闭防攻击拦截器并替换为自定义实现

配置 `tutorials4j.data.mybatis-plus.interceptors.block-attack=false`，然后自己实现 `MybatisPlusInterceptorCustomizer` 并注册。

## 9. 总结

本文分析了一套基于 MyBatis Plus 的企业级数据访问框架，其核心设计思想包括：

- **自动化配置**：利用 Spring Boot 条件装配，按需加载组件。
- **顺序可控的拦截器链**：通过 `Ordered` 接口和常量定义，保证分页、租户、乐观锁等拦截器的正确执行顺序。
- **扩展点开放**：`MybatisPlusInterceptorCustomizer` 允许业务方无侵入地添加自定义拦截器。
- **多租户与审计标准化**：通过 `TenantLineHandler` 和 `MetaObjectHandler` 封装通用逻辑。

该框架已经在多个生产项目中验证，有效降低了数据访问层的重复代码量，并提升了多租户场景下的安全性与维护性。开发者在阅读本文后，可参照类似模式对自己的 MyBatis Plus 应用进行规范化封装。

---

**作者注**：实际使用中，还需谨慎处理租户 ID 的传递（如 Feign 调用、异步线程），以及审计字段的时区、用户信息上下文问题。框架中预留了 `TenantContextHolder` 与 `AuditMetaObjectHandler` 的扩展点，可根据具体环境实现。
