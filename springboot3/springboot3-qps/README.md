# springboot3-qps

## 代码功能介绍

基于提供的代码，该项目实现了一个**方法级 QPS（每秒查询数）与调用耗时监控系统**，适用于 Spring Boot 3 应用。主要功能模块如下：

### 1. 方法调用自动统计（基于 AOP）
- **核心注解**：`@MethodQps`  
  标注在需要监控的服务方法上（如 `DemoService.qps1()`、`DemoService.qps2()`）。
- **切面实现**：`MethodQpsAspect`
    - 使用 `@Around` 环绕通知拦截被 `@MethodQps` 标记的方法。
    - 记录每次调用的**耗时**（纳秒精度，转换为毫秒保存）。
    - 集成 **Micrometer** 指标库：
        - `Counter`：统计方法总调用次数（指标名 `method_calls_total`，标签 `method`）。
        - `Timer`：记录调用耗时分布（指标名 `method_call_duration`），支持百分位数（50%、95%、99%）和 SLA 阈值（10ms、50ms、100ms、500ms）。
    - 同时将最近 **100 次**调用的耗时存入 `CallTimeRecorder`，供后续查询。

### 2. 最近调用时间记录器（`CallTimeRecorder`）
- 使用 **`AtomicReferenceArray`** 固定长度 100 的环形缓冲区，存储每次调用的耗时（毫秒）。
- 提供线程安全的记录方法 `recordCallTime()`。
- 可生成统计报告 `MethodCallStats`，包含：
    - 实际记录次数（最多 100 次）
    - 平均耗时、最小耗时、最大耗时、总耗时
    - 最近每次调用的耗时数组

### 3. 统计信息查询接口
#### REST API（`MethodQpsStatsController`）
- `GET /api/method-qps-stats`：获取所有被监控方法的统计信息（返回 `Map<String, MethodCallStats>`）。
- `GET /api/method-qps-stats/{methodName}`：获取指定方法的统计摘要。
- `GET /api/method-qps-stats/{methodName}/details`：获取指定方法的详细调用信息（含最近 100 次具体耗时）。

#### Web 页面（`MethodQpsMonitorController`）
- `GET /monitor/qps/method`：使用 Thymeleaf 渲染 `method-qps-monitor` 视图，将所有监控方法的统计数据传递给前端页面，实现可视化监控面板。

### 4. HTTP 请求级监控（可选）
- `QpsInterceptor`：自定义 `HandlerInterceptor`，对每一个进入的 HTTP 请求进行拦截。
    - 使用 Micrometer `Counter` 统计总请求数（指标 `http_requests_total`）。
    - 使用 Micrometer `Timer` 记录请求处理耗时（指标 `http_request_duration_seconds`）。
- 注意：该拦截器未在配置类中显式注册，可能需要额外配置才能生效；实际应用中可作为 API 整体流量的监控补充。

### 5. 示例演示（`DemoController` + `DemoService`）
- `DemoController.demo()` 接收到 `/demo` 请求后：
    - 随机生成 0~8 次调用 `demoService.qps1()`。
    - 随机生成 0~8 次调用 `demoService.qps2()`。
- 两个服务方法内部随机休眠 0~998 毫秒，模拟真实耗时业务逻辑，便于观察统计效果。

### 6. 架构特点
- **低侵入性**：通过注解 + AOP 实现监控，业务代码无需改动。
- **线程安全**：使用 `AtomicInteger`、`AtomicReferenceArray` 和 `ConcurrentHashMap` 保证高并发下的数据正确性。
- **指标可观测**：直接对接 Micrometer，可轻松集成 Prometheus、Graphite 等监控后端。
- **实时统计**：保留最近 100 次调用数据，支持快速分析方法性能波动。

### 7. 运行与使用
1. 启动 `SpringBoot3QPSApplication`。
2. 多次调用 `/demo` 接口（例如使用浏览器或 curl）触发被监控方法。
3. 访问监控页面：`http://localhost:8080/monitor/qps/method`
4. 或通过 API 获取 JSON 数据：`http://localhost:8080/api/method-qps-stats`

---

通过以上设计，开发者可以方便地**统计任意方法的调用次数、耗时分布、最近执行详情**，为性能分析和容量规划提供数据支持。