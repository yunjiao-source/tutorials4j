该代码实现了一个基于 Spring Boot 的 SSE（Server-Sent Events）演示应用，包含两种不同的技术方案：

### 1. 响应式 SSE（WebFlux） – `NewsController`
- 路径：`/news/live`
- 每秒推送一条**公开新闻**，数据格式为 `ServerSentEvent<String>`
- 事件类型固定为 `news-update`，数据内容为当前时间（例如 `Latest update at: 14:30:25`）
- 使用 `Flux.interval` 定时生成事件流，适合高并发、非阻塞场景

### 2. 传统 MVC SSE（SseEmitter） – `NotificationController` + `NotificationService`
- 路径：`/notifications/{userId}`
- 为每个用户维护独立的 `SseEmitter` 连接（存于 `ConcurrentHashMap`）
- 可通过 `sendToUser(userId, message)` 向**特定用户**发送私有通知
- 示例中通过 `@Scheduled` 定时任务，每 5 秒向用户 `user123` 发送一条模拟消息（`message: 当前时间`）
- 连接超时设为 1 小时，断开或异常时自动清理映射

### 3. 页面路由 – `PageController`
- `GET /` → 返回 `sse/index` 视图（可能是首页）
- `GET /news` → 返回 `sse/news` 视图，并携带模拟用户 ID `user123`
- `GET /notifications` → 返回 `sse/notifications` 视图，同样传递 `user123`（实际可从 SecurityContext 或 Session 获取真实用户）

### 整体功能
- **演示两种 SSE 实现方式**：WebFlux 响应式流 vs. Servlet 容器兼容的 SseEmitter
- **提供实时推送场景**：
    - 公共广播：所有客户端接收相同的新闻流
    - 私有推送：服务端可精确向指定用户发送通知
- **附带前端页面（需配合模板引擎如 Thymeleaf）**：页面中通过 JavaScript 的 `EventSource` 分别连接 `/news/live` 和 `/notifications/{userId}` 以接收并展示数据

该代码适合学习 Spring Boot 中 SSE 的使用，以及响应式编程与传统 MVC 的对比。