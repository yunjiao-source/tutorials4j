# springboot3-micrometer-annotation

## 功能分析

基于提供的代码和配置，该 Spring Boot 3 应用利用 Micrometer 的 `@Timed` 和 `@Counted` 注解来实现方法级的监控指标收集，并通过自定义 `CountedAspect` 限制计数范围。主要功能点如下：

### 1. 暴露的 REST 接口

| 端点 | 方法 | 行为 | 关联监控注解 |
|------|------|------|--------------|
| `/timed` | GET | 随机休眠 0~500ms，调用 `DemoService.timed()` 返回 `"timed"` | Controller: `@Timed`<br>Service: `@Timed` |
| `/counted/{id}` | GET | 随机休眠 0~500ms，调用 `DemoService.counted(id)`：<br> - 若 `id` 为奇数，返回 `id` 字符串<br> - 若 `id` 为偶数，抛出 `RuntimeException` | Controller: `@Counted`<br>Service: `@Counted`（被自定义切面跳过） |

### 2. Micrometer 指标生成情况

#### 2.1 `@Timed` 注解
- **默认行为**：自动配置的 `TimedAspect` 对所有带 `@Timed` 的方法生效（Controller + Service）。
- **生成的指标**：
    - `demo.controller.timed`：记录 `/timed` 接口的整体耗时（含随机睡眠 + Service 调用）。
        - 额外配置了百分位数：`0.5, 0.9, 0.95, 0.99`。
    - `demo.service.timed`：记录 `DemoService.timed()` 方法的执行耗时。
- **标签**：默认包含 `exception`（若方法抛出异常）、`method`、`class` 等。

#### 2.2 `@Counted` 注解
- **自定义切面**：`MonitorConfiguration` 中显式定义了 `CountedAspect`，并传入 `skipNonControllers` 过滤逻辑。
- **过滤规则**：`skipNonControllers` 返回 `true` 时跳过计数，即 **仅当目标类标注了 `@RestController` 时** 才进行计数。
- **生效范围**：
    - ✅ `DemoController.counted()` —— 会被计数。
    - ❌ `DemoService.counted()` —— 被跳过，不会产生任何指标。
- **生成的指标**：
    - `demo.controller.counted`：记录 `/counted/{id}` 接口的调用次数。
- **标签**：Micrometer 默认会为 `@Counted` 添加 `result`（success/error）和 `exception`（异常类名）标签。因此：
    - 当 `id` 为奇数（正常返回）→ `result=success`，无 `exception` 标签。
    - 当 `id` 为偶数（抛异常）→ `result=error`，`exception=RuntimeException`。

### 3. 配置要点

- **Actuator 暴露**：`include: '*'` 暴露所有端点，包括 `/prometheus`。
- **Prometheus 端点**：`access: read_only` 表示只读访问。
- **全局指标启用**：`management.metrics.enable.all: true` 开启所有默认指标。
- **观测注解支持**：`management.observations.annotations.enabled: true` 开启 `@Observed` 支持（本应用中未使用）。

### 4. 潜在注意事项

#### 4.1 自定义 `CountedAspect` 与自动配置的关系
- Spring Boot 的 Micrometer 自动配置在 `CountedAspect` 存在 `@ConditionalOnMissingBean` 条件，因此自定义的 `CountedAspect` Bean 会**完全替换**默认实现。
- 这导致 Service 层的 `@Counted` 被有意忽略，符合设计意图。

#### 4.2 `skipNonControllers` 判断逻辑
```java
return AnnotationUtils.findAnnotation(targetClass, RestController.class) == null;
```
- 仅检查**类级别**是否有 `@RestController`。如果 `@RestController` 写在父类或接口上，`findAnnotation` 会查找（Spring 的 `AnnotationUtils` 支持继承查找），一般没问题。
- 注意：`@Counted` 注解写在方法上，但过滤依据是**目标类**，而非方法所在的声明类（代理后依然正确）。

#### 4.3 异常对指标的影响
- **`@Counted`**：异常也会被计数（除非配置 `recordFailuresOnly`），但标签会区分 `result=error`。
- **`@Timed`**：异常同样被记录耗时，且会添加 `exception` 标签。

#### 4.4 性能考量
- Controller 和 Service 中都使用了 `Thread.sleep()` 模拟耗时，实际生产环境中应避免阻塞 Servlet 容器线程（可改用 `@Async` 或 WebFlux）。

### 5. 指标验证示例（访问 Prometheus 端点）

假设调用：
```
GET /timed          → 正常返回
GET /counted/1      → 正常返回
GET /counted/2      → 抛出异常
```

在 `/actuator/prometheus` 中可看到类似指标：

```
# HELP demo_controller_timed_seconds_max  
# TYPE demo_controller_timed_seconds_max gauge
demo_controller_timed_seconds_max{...} 0.XX

# HELP demo_controller_counted_total  
# TYPE demo_controller_counted_total counter
demo_controller_counted_total{result="success",} 1.0
demo_controller_counted_total{result="error",exception="RuntimeException",} 1.0

# HELP demo_service_timed_seconds  
demo_service_timed_seconds_sum{...} X.XX
demo_service_timed_seconds_count{...} 1.0
```

**注意**：`demo.service.counted` 指标不会出现，因为被自定义切面跳过。

### 总结
该应用成功演示了：
- 使用 `@Timed` 监控 Controller 和 Service 层的方法耗时。
- 使用 `@Counted` 并**自定义切面**，使其仅统计 Controller 层的调用次数（Service 层的 `@Counted` 被忽略）。
- 通过 Actuator + Prometheus 暴露指标，便于集成监控系统。

