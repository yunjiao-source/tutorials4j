参考： https://mp.weixin.qq.com/s/8-j-aG9Uhe53BfmpImCivg

# 静态配置升级到可热更新的路由控制 (NacosRouteDefinitionRepository)

该代码实现了一个基于 Nacos 的 Spring Cloud Gateway 动态路由仓库，主要功能如下：

- **路由定义来源**：从 Nacos 配置中心读取 JSON 格式的路由配置（固定 dataId `gateway-routes.json`，group `GATEWAY_GROUP`），并转换为 `RouteDefinition` 对象。
- **本地缓存**：使用 `ConcurrentHashMap` 缓存路由定义，避免每次请求都解析配置，提高性能。
- **动态更新**：通过 `@PostConstruct` 初始化时注册 Nacos 监听器，配置变更时自动刷新缓存并发布 `RefreshRoutesEvent`，触发网关路由热更新，无需重启。
- **只读仓库**：重写的 `save` 和 `delete` 方法直接抛出异常，表明路由变更只能通过 Nacos 管理，不支持编程式修改。
- **辅助组件**：依赖 `RouteDefinitionConverter` 进行格式转换，`RouteConfigValidator` 做配置校验，并启用 `enabled` 字段进行路由开关控制。

整体是一个生产级的动态路由解决方案，实现了配置集中管理和实时生效。

# 基于 GlobalFilter 的 trace 与访问日志 (AccessLogGlobalFilter)

这是一个Spring Cloud Gateway的全局过滤器（`GlobalFilter`），主要功能包括：

1. **链路追踪标识（TraceId）管理**
    - 优先从请求头 `X-Trace-Id` 获取，若不存在则自动生成 UUID（去横线）并放入请求头。
    - 将 `traceId` 存入 `ServerWebExchange` 属性和 `MDC`，便于日志输出时关联上下文。

2. **访问日志记录**
    - 记录请求的客户端 IP、HTTP 方法、路径、响应状态码、耗时（毫秒）、路由 ID。
    - 若发生异常，记录异常类型（否则为 `"none"`）。
    - 日志级别为 `INFO`，输出结构化信息（含 `traceId`）。

3. **执行时机与顺序**
    - 优先级 `Ordered` 值为 `-900`（较高），在过滤器链早期执行，确保 `traceId` 尽早注入。
    - 使用 `doOnSuccess` / `doOnError` 在响应完成或异常时记录日志，并在最终 `doFinally` 中清理 MDC，避免线程污染。

4. **异常友好**
    - 即使发生异常，也会记录访问日志，并将错误类型体现在日志中，便于排查。

整体上，该过滤器为网关提供了统一的**请求追踪**和**访问审计**能力，是微服务网关的基础设施组件。

# 染色过滤器 (TrafficColoringGlobalFilter)

这段代码是一个Spring Cloud Gateway全局过滤器，用于实现**流量染色（灰度发布）**，主要功能如下：

1. **读取灰度标识**  
   从请求头 `X-Gray-Tag` 获取显式指定的灰度标签，若存在则直接使用。

2. **基于用户ID动态计算灰度标签**  
   若请求头未携带灰度标签，则通过 `X-User-Id` 计算CRC32值并对100取模，与预设灰度比例（10%）比较，决定是否划入灰度流量（`"gray"`）或稳定流量（`"stable"`）。

3. **传递染色结果**  
   将计算出的标签存入 `exchange` 属性（供后续过滤器或路由使用），并修改请求头 `X-Gray-Tag` 向下游传递，确保全链路灰度标识一致。

4. **执行顺序**  
   `getOrder()` 返回 `-20`，优先级较高，保证在业务过滤前完成染色。

**作用**：支持按用户ID比例灰度发布，也可通过请求头手动覆盖，便于测试或特定用户路由至新版本服务。

# 灰度路由过滤器 (GrayRouteGlobalFilter)

该代码实现了一个Spring Cloud Gateway的全局过滤器，用于**灰度发布场景下的版本路由**。核心功能如下：

1. **流量标识获取**：从`ServerWebExchange`的属性中读取`grayTag`，默认值为`"stable"`。
2. **路由条件判断**：仅当当前路由的URI协议为`lb`（负载均衡）时生效，否则直接放行。
3. **版本映射策略**：
   - `grayTag = "gray"` → 目标版本 `"v2"`（灰度版本）
   - 其他情况 → 目标版本 `"v1"`（稳定版本）
4. **服务实例筛选**：通过`ReactiveDiscoveryClient`获取目标服务的所有实例，并按元数据中的`version`字段过滤出匹配目标版本的实例。
5. **实例选择与转发**：若存在匹配实例，则随机选择一个，将其地址解析为新的请求URL，并替换`GATEWAY_REQUEST_URL_ATTR`属性，实现请求重定向；若无匹配实例，则记录警告并直接放行（回退到默认路由）。
6. **优先级设置**：`getOrder()`返回`10050`，确保在全局过滤器链中按需排序执行。

**作用**：支持根据请求携带的灰度标记动态将流量路由到不同版本的服务实例，实现平滑发布或A/B测试。