# [042][数据模块]Mybatis Plus 数据库级租户：基于多数据源路由的动态隔离实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在多租户系统中，租户数据隔离是核心设计问题之一。常见的隔离方案包括“共享表 + tenant_id 字段”、“共享数据库 + 独立 schema”以及“独立数据库”。本文聚焦于**数据库级（database）**隔离策略，结合 Mybatis Plus 和 Spring 框架，通过动态数据源路由实现租户数据自动切换。我们将基于以下三个核心类进行源码级功能分析：

- `MybatisPlusTenantConfiguration` —— 自动配置入口，根据策略激活不同租户实现。
- `MultipleRoutingDataSource` —— 继承自 `AbstractRoutingDataSource`，实现租户标识到实际数据源的动态映射。
- `DataSourceRoutingManager` —— 路由管理器接口，负责根据租户标识查找或创建数据源。

通过剖析这些组件的协作机制，揭示如何在 Mybatis Plus 体系中无侵入地完成数据库级租户隔离。

---

## 1. 数据库级租户概述

**数据库级租户**意味着每个租户拥有独立的物理数据库（或同一个数据库实例下的不同逻辑库）。相比字段过滤或 schema 隔离，它具有最强的数据安全性，并且天然支持租户级别的资源扩展、备份与迁移。

其核心挑战在于：**如何在不修改业务 SQL 的前提下，将每个请求动态路由到该租户对应的数据库连接**。Mybatis Plus 本身不直接提供数据库级路由能力，但我们可以借助 Spring 的 `AbstractRoutingDataSource` 和 Mybatis Plus 的 `SqlSessionFactoryBeanCustomizer` 实现透明切换。

本文代码中，租户策略通过配置项 `tenant.datasource.strategy=database` 激活，对应的配置类 `DatabaseTenantConfiguration` 会向容器注册一个 `SqlSessionFactoryBeanCustomizer`，将 Mybatis Plus 的 `SqlSessionFactory` 的数据源替换为一个动态路由数据源 `MultipleRoutingDataSource`。

---

## 2. 代码结构解析

涉及三个主要模块：

| 类名 | 职责 |
|------|------|
| `MybatisPlusTenantConfiguration` | 条件化配置：根据 `tenant.datasource.strategy` 的值（`table` / `database`）分别启用表级拦截器或数据库级路由定制器。 |
| `MultipleRoutingDataSource` | 自定义的动态路由数据源，从 `TenantContextHolder` 获取当前租户标识，委托给 `DataSourceRoutingManager` 获取对应的真实数据源。 |
| `DataSourceRoutingManager` | 接口，定义按租户名称查找数据源、添加 JDBC 配置、关闭等操作。其实现类（代码未给出，但可从接口推断）负责管理租户与数据源的映射关系及动态创建。 |

此外，`TenantContextHolder` 是一个线程局部变量工具，用于保存当前请求的租户标识（通常在 Web 过滤器中从请求头或 JWT 解析后设置）。

---

## 3. 关键组件分析

### 3.1 MybatisPlusTenantConfiguration：策略开关

```java
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT,
    name = "datasource.strategy",
    havingValue = "database")
static class DatabaseTenantConfiguration {
    @Bean
    SqlSessionFactoryBeanCustomizer databaseSqlSessionFactoryBeanCustomizer(
            MultipleRoutingDataSource dataSource) {
        return factoryBean -> factoryBean.setDataSource(dataSource);
    }
}
```

- **条件触发**：只有当 `tenant.datasource.strategy=database` 时，该内部配置类才会生效。
- **核心 Bean**：注册一个 `SqlSessionFactoryBeanCustomizer`，它的作用是在 Mybatis Plus 构建 `SqlSessionFactory` 之前，将 `factoryBean` 的数据源设置为 `MultipleRoutingDataSource`。
- **依赖注入**：`MultipleRoutingDataSource` 需要预先定义为 Spring Bean（通常由独立的 `DataSourceConfiguration` 创建）。

> 对比表级租户（`havingValue="table"`），那里注册的是 `MybatisPlusInterceptorCustomizer`，用于添加租户 SQL 拦截器（`SimpleTenantLineInterceptorCustomizer`）。两种策略互斥，配置清晰。

### 3.2 MultipleRoutingDataSource：动态路由数据源

```java
public class MultipleRoutingDataSource extends AbstractRoutingDataSource {
    private final DataSourceRoutingManager dataSourceRoutingManager;

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey instanceof String lookupKeyStr) {
            return dataSourceRoutingManager.determineTargetDataSource(lookupKeyStr);
        }
        throw new IllegalStateException("不支持的多数据源键：" + lookupKey);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.get();
    }
}
```

- **继承 Spring 抽象类**：`AbstractRoutingDataSource` 是多数据源路由的标准实现。它会在每次获取连接时调用 `determineCurrentLookupKey()` 得到路由键，再根据该键从 `targetDataSources` 映射中获取真实数据源。
- **路由键获取**：`determineCurrentLookupKey()` 直接返回 `TenantContextHolder.get()`（例如租户代码 `"tenant_001"`）。
- **数据源解析委托**：`determineTargetDataSource()` 没有使用父类维护的 `targetDataSources` map，而是完全委托给 `DataSourceRoutingManager`。这样设计的原因是：租户数量可能非常多，动态增减，不可能提前将所有数据源注册到 map 中。路由管理器可以按需创建、缓存和销毁数据源。
- **构造方法**：接收路由管理器和默认数据源，并将默认数据源以默认租户键（`DefaultConsts.DEFAULT_TENTANT_CODE`）存入父类的 `targetDataSources`，作为后备。

### 3.3 DataSourceRoutingManager：路由策略抽象

接口定义如下：

```java
public interface DataSourceRoutingManager {
    DataSource determineTargetDataSource(String name);
    DataSource getDefaultDataSource();
    void addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions);
    void shutdown();
}
```

- **`determineTargetDataSource(String name)`**：核心方法，根据租户名返回对应的 `DataSource`。实现类通常维护一个 `Map<String, DataSource>` 缓存，当 name 不存在时，根据预先注册的 `JdbcOptions` 动态创建新的 HikariCP/Druid 数据源并放入缓存。
- **`addRoutingJdbcOptions`**：用于动态注册新租户的 JDBC 连接信息。例如，在租户入驻时调用此接口，将租户的数据库 URL、用户名、密码等存储起来，供后续动态创建数据源使用。
- **`shutdown()`**：在应用关闭时优雅释放所有动态创建的数据源（关闭连接池）。

> 虽然没有给出实现类，但此接口清晰地表达了数据库级租户的核心能力——**按需创建、缓存、销毁租户数据源**。

---

## 4. 工作流程

一个典型的请求处理流程如下：

1. **请求进入过滤器/拦截器**  
   解析请求头（如 `X-Tenant-ID`），调用 `TenantContextHolder.set(tenantId)`。

2. **Mybatis Plus 执行数据库操作**  
   Mybatis 的 `SqlSession` 通过 `SqlSessionFactory` 获取连接。由于 `SqlSessionFactory` 的 `dataSource` 已经被替换为 `MultipleRoutingDataSource`，因此会调用其 `getConnection()`。

3. **动态路由数据源选择**  
   - `MultipleRoutingDataSource.determineCurrentLookupKey()` → 返回 `TenantContextHolder.get()`（租户ID）。  
   - `determineTargetDataSource()` → 调用 `dataSourceRoutingManager.determineTargetDataSource(tenantId)`。  
   - 路由管理器从缓存中查找该租户的数据源；如果不存在，则根据提前注册的 `JdbcOptions`（或从配置中心拉取）动态创建并缓存。

4. **获取真实数据库连接**  
   路由管理器返回真实数据源，`MultipleRoutingDataSource` 再从其 `getConnection()` 获得连接，供 Mybatis 使用。

5. **清除租户上下文**  
   请求结束后，在过滤器 finally 块中调用 `TenantContextHolder.clear()`，避免线程池复用时的污染。

---

## 5. 优缺点分析

### 5.1 优势

- **极强的隔离性**：租户数据物理分离，安全性最高，符合金融、医疗等严格合规场景。
- **无 SQL 侵入**：不需要在每张表中添加 `tenant_id` 字段，也不需要修改业务查询语句。
- **弹性扩展**：可为大租户单独配置更高规格的数据库，甚至迁移到独立数据库实例。
- **Mybatis Plus 无缝集成**：通过 `SqlSessionFactoryBeanCustomizer` 替换数据源，对 Mapper 层完全透明。

### 5.2 挑战与注意事项

- **连接数膨胀**：每个租户独立数据源意味着每个租户至少占用一个连接池，租户数量较大时（例如上千租户）会耗尽数据库连接数。需配合连接池共享（如 HikariCP 的共享连接）或使用云原生数据库代理。
- **动态数据源管理复杂度**：需要自行实现 `DataSourceRoutingManager` 的缓存、懒加载、健康检查、关闭等逻辑。
- **跨租户查询困难**：若需要跨租户聚合统计（如管理员查看所有租户数据），无法单库 SQL 完成，需借助数据仓库或分布式查询引擎。
- **事务边界**：一个请求只能操作一个租户数据库。如果业务需要同时操作多个租户，需使用分布式事务或任务编排。

---

## 6. 与表级租户的对比

| 维度 | 数据库级租户 | 表级租户（字段过滤） |
|------|-------------|---------------------|
| 实现方式 | 动态数据源路由 | Mybatis Plus 拦截器自动拼接 `tenant_id = ?` |
| 数据隔离程度 | 物理隔离（独立库） | 逻辑隔离（共享表） |
| 租户数量上限 | 受限于数据库实例连接数 | 几乎无限制 |
| 运维复杂度 | 高（备份恢复、迁移每个租户库） | 低（统一管理） |
| 跨租户查询 | 困难（需多数据源联合） | 简单（修改过滤条件即可） |
| 适用场景 | 企业级高安全要求、租户数少（<200） | SaaS 通用型、租户数成百上千 |

---

## 7. 总结

本文通过对 `MybatisPlusTenantConfiguration`、`MultipleRoutingDataSource` 和 `DataSourceRoutingManager` 三个核心类的功能剖析，展示了基于 Mybatis Plus 实现数据库级租户隔离的完整设计思路。

该方案的核心价值在于：**利用 Spring 的抽象路由数据源 + Mybatis Plus 的定制扩展点，在不侵入业务代码的前提下，将每个租户的数据库操作透明地路由到其专属数据库**。

开发者只需：
1. 配置 `tenant.datasource.strategy=database`；
2. 实现 `DataSourceRoutingManager` 接口，管理租户数据源的动态创建与缓存；
3. 在请求入口设置 `TenantContextHolder`。

即可获得一套简洁、安全、可扩展的数据库级多租户架构。当然，生产落地时还需考虑连接池上限、数据源监控、事务一致性等进阶问题，但本文提供的代码骨架已经为这些增强留出了清晰的扩展点。
