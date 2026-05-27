根据提供的代码文件，这是一个基于 Spring Boot 3 和 `ResponseBodyEmitter` 实现的 **服务端流式推送（Server-Sent Events 风格）** 示例集合。主要功能包括：

### 1. **基础流式日志推送**（`StreamController`）
- 端点：`GET /stream/logs`
- 行为：建立 `ResponseBodyEmitter` 长连接，每秒向客户端推送一条带时间戳的模拟日志。
- 特性：连接建立时立即发送“连接成功”消息；支持连接关闭/超时回调；使用单线程调度器周期性推送。

### 2. **实时进度条更新**（`ProgressController` + `ProgressService`）
- 端点：`GET /stream/progress/{taskId}`
- 行为：模拟一个耗时任务（共8步，每步随机延迟0~500ms），通过 `ResponseBodyEmitter` 逐步推送进度百分比和描述。
- 特性：支持任务ID隔离；连接关闭/超时处理；任务完成后自动关闭流。

### 3. **实时日志推送服务**（`RealTimeLogService`）
- 功能：管理多个基于 `(userId, logType)` 组合键的流式连接。
- 提供方法：
    - `createLogStream(userId, logType)`：创建并存储 emitter，返回给客户端。
    - `pushLogToUser(userId, logType, logMessage)`：向特定用户的特定日志类型推送消息。
    - `pushLogToAll(logMessage)`：向所有活跃连接广播消息。
- 特性：连接关闭时自动清理；发送失败时移除无效连接。

### 4. **批量缓存与推送**（`BatchStreamService`）
- 功能：为每个 `streamId` 维护一个消息缓冲区，并支持定时批量发送。
- 提供方法：
    - `addToBuffer(streamId, message)`：将消息加入缓冲区。
    - `startBatchProcessing(streamId, batchSize, intervalMs)`：启动一个定时任务，按固定时间间隔从缓冲区中取出最多 `batchSize` 条消息，批量发送（实际发送逻辑在 `sendBatchToStream` 中待实现）。
- 特性：使用 `ConcurrentHashMap` 和同步块保证线程安全；批量发送可降低网络开销。

### 5. **连接池管理**（`StreamConnectionPool`）
- 功能：统一管理多个 `ResponseBodyEmitter` 及其关联的调度器。
- 提供方法：
    - `createStream(streamId)`：创建 emitter，同时创建一个单线程调度器用于后续定时任务，并存储在 Map 中；在连接完成/超时时自动清理调度器。
    - `sendToStream(streamId, data)`：向指定流发送数据（自动追加换行），发送失败则关闭 emitter。
- 特性：将连接生命周期与调度器生命周期绑定，防止资源泄漏。

### 6. **进度记录对象**（`Progress`）
- 一个简单的 Java record，包含三个字段：
    - `percentage`：当前进度百分比（0~100）
    - `description`：进度描述文本
    - `completed`：是否已完成

---

## 整体功能定位
这些代码共同演示了 **Spring MVC 中非阻塞、异步、流式响应** 的典型应用场景：
- 模拟 **实时日志推送**（如系统日志、应用日志）
- 模拟 **长任务进度通知**（如文件上传、数据处理）
- 提供 **批量消息缓存与定时发送** 的优化策略
- 实现 **多客户端连接池管理** 和 **定向/广播推送**

适用于需要服务端主动向 Web 前端（或移动端）持续推送数据的场景，替代 WebSocket 或 SSE 的简化实现。所有示例均未包含前端页面，仅提供后端 API 能力。