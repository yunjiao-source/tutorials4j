# mybatis-tenant

该代码实现了一个基于 Spring Boot 3 + MyBatis Plus 的多租户（Multitenancy）Web 应用，主要功能如下：

1. **多租户隔离**
    - 通过 `TenantContext`（ThreadLocal）存储当前请求的租户 ID。
    - 在 `MyBatisPlusConfig` 中配置 MyBatis Plus 的 `TenantLineInnerInterceptor` 插件，自动为涉及 `user_mybatis_tenant` 表的 SQL 语句追加租户条件（如 `WHERE tenant_id = ?`），同时忽略公共表（`common_table`）。
    - 租户 ID 的值从 `TenantContext` 中动态获取。

2. **租户传递**
    - `TenantHandlerInterceptor` 拦截所有 HTTP 请求，从请求头 `X-Tenant-ID` 中读取租户 ID，并设置到 `TenantContext` 中。
    - 请求结束后自动清除租户 ID，避免线程污染。

3. **用户管理 API**
    - 提供 REST 接口 `/users`：
        - `GET`：查询当前租户下的所有用户。
        - `POST`：创建新用户（需传入 `name`），插入数据时会自动填充当前租户的 ID 到表中。
    - 实体 `User` 映射表 `user_mybatis_tenant`，包含 `id` 和 `name` 字段（表中需额外存在租户列，如 `tenant_id`，由插件自动处理）。

**注意**：实体 `User` 中未显式定义租户字段，但 MyBatis Plus 多租户插件默认要求表中有租户列（例如 `tenant_id`），实际使用时应确保数据库表结构匹配，或通过配置指定租户列名。