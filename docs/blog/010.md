#  [010][数据模块]多数据源管理器在 Hibernate 多租户中的应用

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 1. 背景与挑战

在现代多租户系统中，租户数据隔离策略常见的有三种：独立数据库、共享数据库独立 Schema、共享数据库共享表（通过 tenant_id 区分）。其中，**独立数据库**策略为每个租户单独分配一个物理数据库，安全性最高，但也引入了新的技术挑战——应用程序如何在运行时动态切换数据源。

Hibernate 作为 JPA 规范的主流实现，原生支持多租户（Multi-tenancy），其中 `DATABASE` 模式允许每个租户请求使用不同的 `DataSource`。要实现这一模式，开发者需要提供一个 `MultiTenantConnectionProvider` 实现，负责根据租户标识返回对应的连接（或数据源）。

本文将以 tutorials4j 框架为例，深入剖析如何基于一个**可插拔的多数据源管理器** （`DataSourceRoutingManager`） 优雅地实现 Hibernate 数据库级多租户。该管理器支持 HikariCP、DBCP2、Druid 三大主流连接池，并采用“模板复制”策略为每个租户动态创建独立的数据源实例。

## 2. 整体架构设计

整个方案的核心组件及其关系如下图所示：

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot AutoConfiguration           │
│  (TenantHibernateConfiguration)                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  @ConditionalOnProperty("tenant.datasource.strategy  │ │
│  │                         =hibernate_database")        │ │
│  │  - 创建 DataSourceRoutingManagerCreator Bean          │ │
│  │  - 创建 TenantDataSourceBasedMultiTenantConnectionProvider │ │
│  └──────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│           DataSourceRoutingManagerCreator                   │
│  - 持有默认 DataSource                                       │
│  - 根据默认DataSource类型（Hikari/DBCP2/Druid）创建具体Manager │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│          DataSourceRoutingManager (接口)                     │
│  - getDefaultDataSource()                                   │
│  - determineTargetDataSource(tenant) → DataSource          │
│  - addRoutingJdbcOptions(name, JdbcOptions)                │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────────┐
              ▼               ▼                   ▼
     HikariMapData     Dbcp2MapData        DruidMapData
     SourceRouting     SourceRouting        SourceRouting
     Manager           Manager              Manager
              │               │                   │
              └───────────────┴───────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│   TenantDataSourceBasedMultiTenantConnectionProvider       │
│   (实现 Hibernate MultiTenantConnectionProvider)            │
│   - selectAnyDataSource() → 默认数据源                       │
│   - selectDataSource(tenant) → manager.determine...        │
└─────────────────────────────────────────────────────────────┘
```

核心思想：
- **配置驱动**：通过 `tenant.datasource.strategy=hibernate_database` 激活独立数据库多租户模式。
- **管理器抽象**：`DataSourceRoutingManager` 定义统一的租户数据源路由接口。
- **池化适配**：不同连接池有各自的配置复制方式，各子类负责实现。
- **懒加载与缓存**：为每个租户创建的 `DataSource` 会被缓存，避免重复初始化开销。
- **Hibernate 集成**：自定义 `MultiTenantConnectionProvider` 将路由逻辑桥接到 Hibernate 多租户机制。

## 3. 多数据源管理器的核心实现

### 3.1 抽象层：`AbstractDataSourceRoutingManager`

该类存储了两个关键信息：
- `defaultDataSource`：主数据源（来源于 Spring Boot 自动配置的 `DataSource` Bean）。
- `jdbcOptionsMap`：租户标识到 `JdbcOptions`（包含 URL、用户名、密码等）的映射，这些配置通常由外部配置源（如 `application.yml`）提供。

关键方法：
- `determineTargetDataSource(String name)` 由子类实现。
- `addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions)` 供外部注入租户 JDBC 配置。
- `createDataSource(String name)` 模板方法：从 `jdbcOptionsMap` 获取配置，再调用抽象方法 `createDataSource(String tenant, JdbcOptions)`。

### 3.2 缓存层：`AbstractMapDataSourceRoutingManager`

该类继承上述抽象类，并引入 `ConcurrentHashMap<String, DataSource> targetDataSources` 作为租户数据源缓存。其 `determineTargetDataSource` 实现如下：

```java
public DataSource determineTargetDataSource(String name) {
    return targetDataSources.computeIfAbsent(name, this::createDataSource);
}
```

`computeIfAbsent` 保证了线程安全性，且仅在第一次请求某个租户时才创建对应的数据源。初始化时，它会将默认数据源以固定 key（`DefaultConsts.DEFAULT_TENTANT_CODE`）放入缓存。

### 3.3 连接池适配器

不同的连接池拥有不同的配置复制 API。以 HikariCP 为例：

```java
public class HikariMapDataSourceRoutingManager extends AbstractMapDataSourceRoutingManager {
    @Override
    protected DataSource createDataSource(String tenant, JdbcOptions options) {
        HikariDataSource original = (HikariDataSource) getDefaultDataSource();
        HikariConfig config = new HikariConfig();
        original.copyStateTo(config);          // 复制全部现有配置
        config.setJdbcUrl(options.getUrl());   // 覆盖租户特有配置
        config.setUsername(options.getUsername());
        config.setPassword(options.getPassword());
        return new HikariDataSource(config);
    }
}
```

对于 DBCP2 和 Druid，则通过手工复制关键属性（如 `maxTotal`, `maxWait`, `validationQuery` 等），确保每个租户的数据源拥有与默认数据源一致的连接池行为，仅连接地址和凭证不同。

这种“克隆默认数据源配置”的策略避免了硬编码连接池参数，且能自动跟随主数据源的配置变更（重启后生效）。

### 3.4 工厂创建器：`DataSourceRoutingManagerCreator`

`DataSourceRoutingManagerCreator` 实现了 `Supplier<DataSourceRoutingManager>`，其主要职责是**根据默认数据源的实际类型**来决定实例化哪种 `AbstractMapDataSourceRoutingManager` 子类：

```java
switch (dataSource.getClass().getName()) {
    case "com.zaxxer.hikari.HikariDataSource" -> return new HikariMapDataSourceRoutingManager();
    case "org.apache.commons.dbcp2.BasicDataSource" -> return new Dbcp2MapDataSourceRoutingManager();
    case "com.alibaba.druid.pool.DruidDataSource" -> return new DruidMapDataSourceRoutingManager();
    default -> throw new DataSourceNotSupportException(...);
}
```

该 Bean 在 `DataJdbcConfiguration` 中被初始化，并传入主数据源。

## 4. Hibernate 多租户集成

### 4.1 自动配置触发条件

`TenantHibernateConfiguration` 中有两个内部配置类：
- `TableTenantConfiguration`：当 `strategy = hibernate_table` 时启用，用于共享表模式（仅注册 `CurrentTenantIdentifierResolver`，此模式不涉及动态数据源切换）。
- `DatabaseTenantConfiguration`：当 `strategy = hibernate_database` 时启用，这是本文重点。

在 `DatabaseTenantConfiguration` 中，会创建 `TenantDataSourceBasedMultiTenantConnectionProvider` Bean，并注册到 Hibernate 属性中。

### 4.2 MultiTenantConnectionProvider 实现

`TenantDataSourceBasedMultiTenantConnectionProvider` 继承自 Hibernate 的 `AbstractDataSourceBasedMultiTenantConnectionProviderImpl`，并同时实现了 `HibernatePropertiesCustomizer` 以便将自己注入 Hibernate 配置。

核心方法：

```java
@Override
protected DataSource selectAnyDataSource() {
    return manager.getDefaultDataSource();   // fallback 数据源
}

@Override
protected DataSource selectDataSource(String tenantIdentifier) {
    return manager.determineTargetDataSource(tenantIdentifier);
}

@Override
public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
}
```

当 Hibernate 需要为某个租户获取连接时，会调用 `selectDataSource(tenantIdentifier)`，该方法委托给 `DataSourceRoutingManager` 的路由逻辑，返回该租户专属的 `DataSource`。`selectAnyDataSource` 用于一些不区分租户的 Hibernate 内部操作（如启动时的 Schema 验证）。

### 4.3 租户标识解析器

为了完整支持 Hibernate 多租户，还需提供 `CurrentTenantIdentifierResolver` 实现（示例中的 `DefaultCurrentTenantIdentifierResolver`），它负责从当前上下文（如 ThreadLocal、请求头）中解析出当前租户 ID，并设置到 Hibernate 会话中。由于该部分偏业务，框架提供了一个简单实现供扩展。

## 5. 配置与使用示例

### 5.1 添加依赖

确保项目中引入了 `tutorials4j-framework-tenant-hibernate` 以及对应的连接池驱动。

### 5.2 application.yml 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/master_db
    username: root
    password: root
    hikari:
      maximum-pool-size: 20

tenant:
  datasource:
    strategy: hibernate_database   # 开启数据库级多租户
    jdbc:
      tenant_a:
        url: jdbc:mysql://localhost:3306/tenant_a_db
        username: tenant_a_user
        password: a_password
      tenant_b:
        url: jdbc:mysql://localhost:3306/tenant_b_db
        username: tenant_b_user
        password: b_password
```

上面的配置中，`spring.datasource` 为默认数据源，其连接池配置（如 Hikari 参数）会被复制给所有租户数据源。`tenant.datasource.jdbc` 下每个租户的配置会通过 `addRoutingJdbcOptions` 注册到路由管理器中。

### 5.3 在业务代码中使用

开发者无需手工操作 `DataSource`，只需在每次请求前（例如通过过滤器或 Spring `RequestContextListener`）设置当前租户标识：

```java
TenantContext.setCurrentTenantId("tenant_a");
// 之后 Hibernate 的所有数据库操作将自动路由到 tenant_a_db
```

## 6. 设计优点与注意事项

### 6.1 优点

- **连接池无关性**：上层代码只依赖 `DataSourceRoutingManager` 接口，实际连接池实现可平滑切换。
- **配置复用**：租户数据源继承默认数据源的全部连接池调优参数，避免重复配置。
- **懒加载**：租户数据源按需创建，避免启动时初始化所有租户连接池。
- **缓存与泄漏防护**：缓存保证每个租户只创建一个数据源实例，连接池自身管理连接生命周期。
- **Hibernate 无缝集成**：完全遵循 Hibernate 多租户 SPI，无需修改现有 Entity 或 DAO 代码。

### 6.2 注意事项

- **数据源关闭**：当应用关闭时，需要遍历 `targetDataSources` 并逐一调用 `close()`（如 `HikariDataSource.close()`），否则可能造成连接泄漏。目前框架未展示这部分，实际生产环境需补充 `@PreDestroy` 钩子。
- **配置动态更新**：`jdbcOptionsMap` 目前只能在初始化阶段注入，若要支持运行时新增租户，需扩展接口。
- **事务边界**：Hibernate 多租户要求在同一事务内租户标识不变。确保在开始事务前设置租户 ID。
- **连接池验证**：像 Druid 的 `copyDataSource` 方法需要捕获 `SQLException` 并适当处理。

## 7. 总结

本文介绍的 `DataSourceRoutingManager` 多数据源管理器与 Hibernate 多租户的结合，为独立数据库模式的租户隔离提供了一个高度可复用、配置化的解决方案。通过连接池模板复制策略，避免了为每个租户编写繁琐的 `DataSource` Bean 定义；通过 Hibernate 标准 SPI 的适配，让 ORM 层完全透明地支持多租户。

该方案已在多个生产项目中验证，支持 HikariCP、DBCP2、Druid 三种主流连接池，且易于扩展至其他连接池。如果您正在构建多租户 SaaS 应用，采用这一模式将大幅降低数据源路由的复杂度，让您更专注于业务逻辑本身。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e