# springboot3-referer

该代码实现了一个基于 **Referer 头** 和 **客户端 IP** 双重校验的图片访问安全控制机制，核心功能如下：

### 1. 配置类 `ImageSecurityConfig`
- 从配置文件（如 `application.properties` / `application.yml`）读取两个白名单：
    - `allowedOrigins`：允许的 Referer 来源（默认 `http://localhost:8080`）
    - `allowedIps`：允许的客户端 IP（默认 `0:0:0:0:0:0:0:1`，即 IPv6 的 localhost）
- 注册 `ImageSecurityFilter` 过滤器，并使其拦截所有 `/images/*` 路径的请求。

### 2. 过滤器 `ImageSecurityFilter`
对每个被拦截的请求执行以下校验逻辑：

#### 2.1 Referer 校验（可选）
- 获取请求头中的 `Referer` 字段。
- **若 Referer 存在**：检查它是否以 `allowedOrigins` 中的任意一个值为前缀。
    - 是 → 通过本步骤
    - 否 → 直接返回 **403 Forbidden**
- **若 Referer 不存在**：跳过该校验（视为通过）。

#### 2.2 客户端 IP 校验（强制）
- 通过 `request.getRemoteAddr()` 获取客户端 IP。
- 如果该 IP **不在** `allowedIps` 列表中，直接返回 **403 Forbidden**。

#### 2.3 放行
- 只有同时满足上述规则（Referer 不存在或通过校验 **且** IP 在白名单内）的请求，才会继续执行后续的过滤链，正常访问 `/images/` 下的资源。

### 3. 典型应用场景
- **防止图片盗链**：限制只有特定网站（Referer）才能引用图片。
- **限制访问来源**：只允许特定 IP 地址（如内网服务器、管理员主机）访问敏感图片资源。
- **双重保障**：即使伪造 Referer，若 IP 不在白名单内也会被拦截；反之内部 IP 发起的请求即使没有 Referer 也能通过。

> ⚠️ 注意：`Referer` 头可被客户端伪造或删除，因此该方案仅能防范普通盗链，不能作为高安全级别的唯一依据。IP 校验相对可靠，但需结合实际网络环境（如代理、负载均衡）调整获取真实 IP 的方式。