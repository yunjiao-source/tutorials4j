# routing-datasource-jpa

该代码实现了一个基于租户标识的多数据源动态路由功能，主要用于多租户场景下根据不同租户自动切换数据库连接。具体功能分析如下：

### 核心组件与职责

| 组件 | 作用 |
|------|------|
| `DataSourceConfig` | 读取配置文件中的多租户数据源信息（`tenants`），为每个租户创建 `HikariDataSource`，并配置 `TenantRoutingDataSource` 作为主数据源，同时设置默认数据源（代码中默认为 `tenant1`，但配置文件中实际为 `tenant-a`/`tenant-b`）。 |
| `TenantRoutingDataSource` | 继承 `AbstractRoutingDataSource`，通过 `determineCurrentLookupKey()` 从 `DataSourceContextHolder` 获取当前线程的租户ID，从而动态选择对应的真实数据源。 |
| `DataSourceContextHolder` | 使用 `ThreadLocal` 存储当前请求的租户ID，提供 `set`、`get`、`clear` 方法，确保线程隔离。 |
| `TenantInterceptor` | 拦截所有 HTTP 请求，从请求头 `X-Tenant-ID` 中提取租户标识并存入 `DataSourceContextHolder`；若无该头则默认设为 `"tenant_a"`。请求结束后清除上下文。 |
| `WebConfig` | 注册 `TenantInterceptor` 拦截器，作用于所有路径。 |
| `DemoController` | 提供 `/cursorUsers` 接口，调用 `UserRepository` 查询数据。此时 `UserRepository` 会通过路由数据源访问当前租户对应的数据库。 |

### 工作流程
1. 客户端发起请求，携带请求头 `X-Tenant-ID: tenant-a`（或 `tenant-b`）。
2. `TenantInterceptor` 拦截请求，将租户ID存入 `ThreadLocal`。
3. `DemoController` 执行 `userRepository.findAll()` 时，JPA 通过 `DataSource` 获取连接。
4. `TenantRoutingDataSource` 调用 `determineCurrentLookupKey()` 从 `ThreadLocal` 拿到当前租户ID，查找对应的 `HikariDataSource`（如 `tenant-a` → `jdbc:postgresql://localhost:5432/demo`）。
5. 数据操作在对应租户的数据库中执行。
6. 请求结束后，拦截器清理 `ThreadLocal`，避免内存泄漏。

### 存在的问题（潜在缺陷）
- **默认租户不一致**：`DataSourceConfig` 中默认数据源为 `tenant1`，但配置文件中只有 `tenant-a` / `tenant-b`，会导致默认数据源为 `null`，启动或运行时可能出错。应改为 `tenant-a`。
- **租户ID命名不统一**：拦截器默认值 `"tenant_a"`（下划线）与配置 `tenant-a`（短横）不一致，会导致默认场景下找不到数据源。
- **缺少租户有效性校验**：如果请求头传入一个未配置的租户ID，`targetDataSources.get(tenantId)` 会返回 `null`，`AbstractRoutingDataSource` 会抛出异常。建议添加校验或回退逻辑。
- **事务管理注意事项**：由于数据源在事务启动时确定，若同一事务内切换租户（例如在 Service 层修改 ThreadLocal），不会重新选择数据源，可能导致数据错乱。需确保租户切换发生在事务边界之前。

### 适用场景
- SaaS 应用，不同租户使用独立数据库（Database per Tenant 模式）。
- 需要根据请求上下文动态切换数据源的其他多数据源场景。

该实现是一个经典的多租户数据源路由方案，结构清晰，但在细节上需要根据实际配置进行调整和加固。

