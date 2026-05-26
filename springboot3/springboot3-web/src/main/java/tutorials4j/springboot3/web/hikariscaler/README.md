# hikari-scaler

## 功能分析总结

您提供的代码实现了一个**基于 HikariCP 的数据库连接池动态扩缩容与监控告警系统**，运行在 Spring Boot 3 环境中。其核心功能如下：

### 1. 实时监控指标采集
- 通过 `ConnectionPoolMonitor` 定时（默认 10 秒）调用 HikariCP 的 JMX 接口（`HikariPoolMXBean`、`HikariConfigMXBean`）获取当前连接池状态：
    - 活跃连接数、空闲连接数、总连接数
    - 等待获取连接的线程数（`pendingThreads`）
    - 当前最大池大小、最小空闲连接数
    - 计算使用率 = 活跃连接数 / 最大池大小 × 100%
- 指标存入内存队列 `metricsHistory`，并自动清理过期数据（保留时长可配置，默认 60 分钟）。

### 2. 动态扩缩容决策与执行
`PoolScalerComponent` 负责判断是否需要调整连接池规模，并执行实际修改。

#### 扩容条件（满足任一即可）
- 当前使用率 > 阈值（默认 80%）
- 等待线程数 ≥ 阈值（默认 5）
- 同时检查：
    - 扩容功能启用
    - 未达到配置的最大连接数上限（默认 100）
    - 距离上次扩缩容超过冷却时间（默认 30 秒）
- 扩容动作：每次增加 `incrementSize`（默认 10）个连接，并同步提高 `minimumIdle`（加 5，不超过新的最大连接数）。

#### 缩容条件
- 使用率持续低于阈值（默认 30%）
- 持续时长由 `idle-time`（默认 1 分钟）与监控间隔共同决定，通过计数器 `consecutiveLowUsage` 累计满足条件的监控周期数。
- 同时检查：
    - 缩容功能启用
    - 当前最大连接数大于最小连接数下限（默认 5）
- 缩容动作：每次减少 `decrementSize`（默认 5）个连接，并同步降低 `minimumIdle`（减 5，但不低于下限）。

### 3. 告警通知
- **高使用率告警**：使用率超过 `high-usage-threshold`（默认 90%）。
- **等待队列告警**：只要有等待线程（`pendingThreads > 0`）即触发（按代码逻辑）。
- 通知服务 `NotificationService` 目前只输出日志，预留了集成邮件、钉钉等渠道的扩展点。

### 4. REST API 管理接口
`PoolController` 提供：
- `GET /api/pool/status` – 当前指标 + 配置的最大/最小连接数
- `GET /api/pool/metrics/history` – 历史指标列表
- `POST /api/pool/scale?targetSize=xxx` – 手动调整最大连接数
- `GET /api/pool/config` – 查看 HikariCP 核心配置（超时、生命周期等）

### 5. 测试辅助功能
- `TestDataCreateRunner`：启动时自动生成 1000 条测试用户数据（使用 JavaFaker）。
- `SimulationComponent` + `TestExecController`：提供 `/test-exec?concurrentRequests=&durationSeconds=` 接口，用于模拟高并发负载，验证动态扩缩容效果。
- 自定义线程池（最大 200 线程）用于执行测试任务。

### 6. 配置灵活性
所有动态扩缩容参数均通过 `application.yml` 中的 `dynamic.pool` 前缀配置，支持启用/禁用，且预置了“保守策略”和“激进策略”的注释示例。

---

## 潜在问题与改进建议

### 1. 缩容持续时长计算不精确
```java
int requiredCount = (int)(properties.getScaleDown().getIdleTime().toSeconds() / properties.getMonitor().getIntervalTime().toSeconds()) + 1;
```
- 使用整数除法可能丢失精度，且未考虑监控间隔单位不一致（`intervalTime` 可能为秒、毫秒等）。建议改用 `Duration` 的 `toMillis()` 精确计算周期数。
- 更健壮的做法：记录首次低使用率时间戳，比较当前时间与首次时间差。

### 2. 冷却时间影响缩容
当前代码中，`evaluate()` 方法**仅在冷却期内跳过评估**，导致缩容也可能被冷却时间阻止。但实际业务场景中，扩容后的冷却期不应影响缩容（否则高负载后连接数无法及时回缩）。建议将冷却时间仅应用于**扩容**，或分别配置扩容冷却和缩容冷却。

### 3. 等待队列告警阈值未使用
`DynamicPoolProperties.Alert` 中定义了 `queueWaitThresholdTime`（等待时间阈值），但 `ConnectionPoolMonitor.checkAlerts()` 并未使用该参数，而是直接以 `pendingThreads > 0` 触发告警。应改为检查连接请求的平均等待时间或超时计数。

### 4. 手动调整后与自动扩缩容状态不一致
`PoolController.manualScale()` 直接修改 `maximumPoolSize`，但未更新 `PoolScalerComponent` 中的 `lastScaleTime` 和 `consecutiveLowUsage` 计数器。手动调整后，自动扩缩容可能立即基于旧的状态再次触发。建议手动调整时同步重置相关状态或记录一次人工干预时间。

### 5. 缺少对 `minimumIdle` 的上下限保护
`executeScale()` 中调整 `minimumIdle` 时：
- 扩容：`Math.min(targetSize, configMXBean.getMinimumIdle() + 5)`
- 缩容：`Math.max(properties.getScaleDown().getMinPoolSize(), configMXBean.getMinimumIdle() - 5)`
  若 `minimumIdle` 初始值本身大于目标 `maxPoolSize`，可能导致逻辑异常。应确保 `minimumIdle` 始终 ≤ `maximumPoolSize`。

### 6. 线程安全问题
`metricsHistory` 使用 `ConcurrentLinkedQueue` 是安全的，但 `consecutiveLowUsage` 和 `lastScaleTime` 使用了 `AtomicInteger` / `AtomicLong`，而 `PoolScalerComponent` 中未对 `evaluate()` 和 `executeScale()` 加锁。若监控周期极短（如 1 秒），可能出现多次评估同时执行扩缩容（虽然有冷却时间，但冷却检查与修改之间仍有竞争）。建议使用 `synchronized` 或 `ReentrantLock` 保证决策与执行的原子性。

### 7. 监控数据内存存储风险
历史指标全部存在内存中，无持久化。长时间运行或高频率监控会导致队列占用内存持续增长（尽管有保留时间清理）。对于生产环境，建议定期将指标导出到时序数据库（如 Prometheus + Micrometer）。

### 8. 测试组件误用生产线程池
`SimulationComponent` 注入的 `ExecutorService` 来自 `ThreadPoolConfig`，该线程池与业务线程池共用可能干扰真实负载测试。建议测试专用线程池与业务隔离，或仅在测试配置文件中启用。

---

## 整体评价
该系统设计清晰，模块化良好，利用了 HikariCP 原生 JMX 能力实现了自动伸缩，可有效应对突发流量和空闲时段，减少手动干预。配合 REST API 和测试模拟，具备较强的可观测性和验证手段。修复上述细节后，可以满足中小型生产环境的连接池弹性管理需求。

