#  [009][租户模块]基于 Hibernate 的多租户连接提供者设计实战

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 1. 引言：多租户架构与 Hibernate 的支持

在现代 SaaS 应用中，多租户（Multi-Tenancy）是一种常见的数据隔离模式。根据隔离级别，通常分为三种策略：

- **独立数据库（Database）**：每个租户拥有独立的物理数据库。
- **共享数据库独立 Schema（Schema）**：租户共享同一数据库实例，但拥有各自的 Schema。
- **共享表（Table）**：所有租户共享同一套表，通过租户 ID 列区分。

Hibernate 作为 JPA 的权威实现，内置了完善的多租户支持。开发者只需实现两个核心接口：

- `CurrentTenantIdentifierResolver`：负责解析当前线程的租户标识符。
- `MultiTenantConnectionProvider`：负责根据租户标识符返回对应的 JDBC 连接（或数据源）。

本文将以 `tutorials4j` 框架为例，深入剖析一套**生产可用的 Hibernate 多租户连接提供者设计方案**。该方案支持 HikariCP、Druid 和 DBCP2 三种主流连接池，实现了租户数据源的懒加载、配置复用和自动装配。

## 2. 核心抽象：`AbstractMultiTenantConnectionProvider`

框架首先定义了一个抽象基类 `AbstractMultiTenantConnectionProvider<T extends DataSource>`，继承自 Hibernate 提供的 `AbstractDataSourceBasedMultiTenantConnectionProviderImpl`。该基类封装了租户数据源管理的关键逻辑：

```java
public abstract class AbstractMultiTenantConnectionProvider<T extends DataSource>
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
        implements HibernatePropertiesCustomizer {
    
    protected Map<String, T> dataSources = new ConcurrentHashMap<>();
    protected Map<String, TenantDataSourceProperties.ConnectionOptions> dataSourceOptionsMap = new HashMap<>();
    
    protected abstract T createDataSource(String tenant, TenantDataSourceProperties.ConnectionOptions options);
    protected abstract T getDefaultDataSource();
    protected abstract void setDefaultDataSource(DataSource dataSource);
}
```

### 2.1 租户数据源缓存

`dataSources` 是一个 `ConcurrentHashMap`，键为租户标识符（字符串），值为对应的连接池数据源实例。这种设计确保了**线程安全**，且支持高并发场景下的快速查找。

### 2.2 懒加载创建

`selectDataSource(String tenantIdentifier)` 方法（由父类定义）使用了 `computeIfAbsent` 模式：

```java
@Override
protected DataSource selectDataSource(String tenantIdentifier) {
    return dataSources.computeIfAbsent(tenantIdentifier, this::createDataSource);
}
```

只有当某个租户**首次**发起数据库请求时，才会触发对应的数据源创建。这大幅减少了应用启动时的资源开销，对于租户数量较多但活跃租户有限的场景尤为友好。

### 2.3 租户配置解析

通过 `init(DataSource dataSource, TenantDataSourceProperties properties)` 方法完成初始化：

- 将租户配置（URL、用户名、密码等）转换为大写键存储，避免租户标识符大小写敏感问题。
- 设置默认数据源（通常是主数据源），同时将默认租户（`DefaultConsts.DEFAULT_TENTANT_CODE`）也放入缓存。

### 2.4 自动注册到 Hibernate

该类实现了 `HibernatePropertiesCustomizer` 接口，在 `customize` 方法中将自己注册到 Hibernate 配置中：

```java
@Override
public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
}
```

这使得 Spring Boot 的 `JpaProperties` 能够自动将该 Provider 合并至 Hibernate 设置，无需手动配置。

## 3. 租户标识解析：`DefaultCurrentTenantIdentifierResolver`

多租户的另一个关键组件是从当前上下文（如 HTTP 请求、RPC 参数）中解析出当前操作属于哪个租户。框架提供了 `DefaultCurrentTenantIdentifierResolver`：

```java
public class DefaultCurrentTenantIdentifierResolver 
        implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
    
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContextHolder.get();
    }
    
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
```

`TenantContextHolder` 是一个线程绑定的上下文工具，通常由拦截器或过滤器在请求入口设置租户 ID。`validateExistingCurrentSessions` 返回 `true` 表示当同一个会话中租户标识发生变化时，Hibernate 会进行校验并抛出异常，这是一种严谨的安全策略。

同样，该类也实现了 `HibernatePropertiesCustomizer`，自动将自身注册为 `MULTI_TENANT_IDENTIFIER_RESOLVER`。

## 4. 具体连接池实现

框架提供了三种连接池的实现，分别对应 HikariCP、Druid 和 DBCP2。每种实现都遵循相同的模式：

- 继承 `AbstractMultiTenantConnectionProvider`，并指定具体的数据源类型泛型。
- 实现 `createDataSource`：基于默认数据源的配置副本，覆盖 URL、驱动、用户名、密码。
- 实现 `getDefaultDataSource` 和 `setDefaultDataSource`，并进行类型校验。

### 4.1 HikariMultiTenantConnectionProvider

HikariCP 是目前性能最高的连接池之一，其 `HikariConfig` 类提供了 `copyStateTo` 方法用于便捷地复制配置：

```java
protected HikariDataSource createDataSource(String tenant, TenantDataSourceProperties.ConnectionOptions options) {
    final HikariConfig hikariConfig = new HikariConfig();
    defaultDataSource.copyStateTo(hikariConfig);   // 复用默认数据源的所有配置
    hikariConfig.setDriverClassName(options.getDriverClassName());
    hikariConfig.setJdbcUrl(options.getUrl());
    hikariConfig.setUsername(options.getUsername());
    hikariConfig.setPassword(options.getPassword());
    return new HikariDataSource(hikariConfig);
}
```

这种设计保证了每个租户的数据源拥有相同的连接池参数（最大连接数、超时时间、验证查询等），仅变更实际连接的数据库地址和凭证。

### 4.2 DruidMultiTenantConnectionProvider

Druid 是阿里巴巴开源的数据库连接池，功能丰富（包括监控、SQL 防火墙等）。其复制过程需要手动拷贝各项参数：

```java
private DruidDataSource copyDataSource(DruidDataSource original) throws SQLException {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setInitialSize(original.getInitialSize());
    dataSource.setMaxActive(original.getMaxActive());
    dataSource.setMinIdle(original.getMinIdle());
    // ... 省略其他参数拷贝
    dataSource.setFilters(String.join(",", original.getFilterClassNames()));
    return dataSource;
}
```

需要注意的是，Druid 数据源复制后必须调用 `init()` 方法完成初始化，因此 `createDataSource` 方法中显式调用了 `newDataSource.init()`。

### 4.3 Dbcp2MultiTenantConnectionProvider

Apache Commons DBCP2 的 `BasicDataSource` 同样需要手动复制配置参数，包括初始连接数、最大/最小空闲、验证查询、空闲驱逐策略等：

```java
private BasicDataSource copyDataSource(BasicDataSource original) {
    BasicDataSource copy = new BasicDataSource();
    copy.setInitialSize(original.getInitialSize());
    copy.setMaxTotal(original.getMaxTotal());
    copy.setMaxWait(original.getMaxWaitDuration());
    // ... 省略其他参数
    copy.setDurationBetweenEvictionRuns(original.getDurationBetweenEvictionRuns());
    return copy;
}
```

## 5. 自动配置：`TenantHibernateConfiguration`

为了让上述组件能够根据项目依赖和配置自动生效，框架提供了 Spring Boot 自动配置类 `TenantHibernateConfiguration`。

### 5.1 策略条件

该配置类根据 `tenant.datasource.strategy` 属性值区分两种多租户策略：

- **`TABLE`**（共享表）：仅注册 `DefaultCurrentTenantIdentifierResolver`，不注册多数据源 Provider。因为共享表模式下所有租户使用同一个数据源，只需通过租户 ID 列过滤数据。
- **`DATABASE`**（独立数据库）：同时注册租户标识解析器和基于连接池的多租户连接提供者。

### 5.2 连接池条件化

对于 `DATABASE` 策略，配置类会根据类路径下是否存在特定连接池的类，以及当前容器中是否有该类型的单例 `DataSource` Bean，来**自动匹配**对应的 Provider：

```java
@Bean
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnSingleCandidate(HikariDataSource.class)
HikariMultiTenantConnectionProvider hikariMultiTenantConnectionProvider(...) { ... }

@Bean
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnSingleCandidate(DruidDataSource.class)
DruidMultiTenantConnectionProvider druidMultiTenantConnectionProvider(...) { ... }

@Bean
@ConditionalOnClass(BasicDataSource.class)
@ConditionalOnSingleCandidate(BasicDataSource.class)
Dbcp2MultiTenantConnectionProvider dbcp2MultiTenantConnectionProvider(...) { ... }
```

这意味着：

- 如果项目使用了 `spring-boot-starter-data-jpa`（默认引入 HikariCP），则会自动生成 `HikariMultiTenantConnectionProvider`。
- 如果手动引入了 Druid 并配置了 `DruidDataSource`，则会自动切换到 Druid 实现。
- 无需任何额外编码，框架零侵入完成适配。

### 5.3 初始化流程

每个 Provider 在 Spring 容器中创建后，会调用 `init(dataSource, properties)` 方法。该方法接收默认数据源和租户配置属性，完成缓存构建。默认数据源通常就是 Spring Boot 自动配置的主数据源（例如从 `application.yml` 中读取的 `spring.datasource` 配置）。

## 6. 使用指南

### 6.1 引入依赖

在项目中添加 `tenant-spring-boot-starter`（示例名称），并确保已包含 Hibernate 和 Spring Boot JPA 模块。

### 6.2 配置多租户策略

在 `application.yml` 中配置：

```yaml
tenant:
  datasource:
    strategy: DATABASE   # 或 TABLE
    connections:
      TENANT_A:
        url: jdbc:mysql://host1:3306/db_a
        username: user_a
        password: pwd_a
        driver-class-name: com.mysql.cj.jdbc.Driver
      TENANT_B:
        url: jdbc:mysql://host2:3306/db_b
        username: user_b
        password: pwd_b
        driver-class-name: com.mysql.cj.jdbc.Driver
```

`connections` 下的键即为租户标识符，框架会自动将其转为大写进行匹配。

### 6.3 实现租户上下文传递

在请求入口（如 Spring MVC 拦截器、网关过滤器）中设置当前租户：

```java
TenantContextHolder.set("TENANT_A");
```

当请求进入 Service 层执行数据库操作时，`DefaultCurrentTenantIdentifierResolver` 会从 `TenantContextHolder` 中获取租户 ID，Hibernate 再通过 `MultiTenantConnectionProvider` 获取对应的数据源，整个过程对业务代码完全透明。

### 6.4 注意事项

- 租户数据源的创建是**懒加载**的，首次访问某个租户时会触发数据源初始化，可能存在毫秒级的延迟。建议在系统启动后对预期租户进行预热，或配置连接池的 `initialSize` 参数。
- 由于每个租户独立持有连接池，需留意数据库连接总数是否超过上限。可根据租户的负载情况调整 `maxTotal` / `maximumPoolSize` 等参数。
- 对于 `DATABASE` 策略，默认数据源本身也会作为一个租户（键为 `DEFAULT`）使用，因此需确保其 URL、账号等配置正确。

## 7. 总结

本文从源码层面剖析了一套基于 Hibernate 的可扩展多租户连接提供者实现框架。该框架具有以下亮点：

- **抽象清晰**：`AbstractMultiTenantConnectionProvider` 封装了租户缓存、懒加载和自动注册等通用逻辑，子类只需关注连接池特定的复制细节。
- **连接池无关**：同时支持 HikariCP、Druid、DBCP2，利用 Spring Boot 的条件注解实现无侵入的自动适配。
- **策略分离**：通过 `strategy` 属性区分共享表模式和独立数据库模式，避免不必要的组件加载。
- **生产就绪**：线程安全 Map 保证高并发下的稳定性；租户配置键大小写归一化避免偶发错误。

对于正在构建多租户 SaaS 应用的团队，这套设计可以直接借鉴或扩展，快速落地基于 Hibernate 的高性能多租户数据源管理方案。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e