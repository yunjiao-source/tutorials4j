基于 `CompletableFuture` 实现异步编排、超时控制、重试机制等常见并发模式。

---

### 1. `BranchOrchestrationDemo.java` – 分支编排
- **功能**：根据用户等级（`level`）动态选择不同的异步任务。
- **流程**：
    - 定义 `queryNormalRights()` 和 `queryVipRights()` 两个异步方法，分别返回普通/高级会员权益描述。
    - 若用户等级 ≥ 2，执行 `queryVipRights()`，否则执行 `queryNormalRights()`。
    - 通过 `join()` 阻塞获取结果并打印。

---

### 2. `CompletableFutureWithStream.java` – 批量数据并行处理
- **功能**：对多个用户 ID 并行查询信息，设置超时与降级，最后聚合结果。
- **流程**：
    - 创建自定义线程池（核心 5，最大 10）。
    - 构造 10 个用户 ID。
    - 对每个 ID 调用 `queryUser()`（模拟耗时 500ms），并添加 `orTimeout(1秒)` 和 `exceptionally` 降级处理。
    - 使用 `stream().map(CompletableFuture::join)` 等待所有任务完成，收集结果。
    - 输出所有用户信息及总耗时。

---

### 3. `CompleteOnTimeoutDemo.java` – 缓存查询超时降级
- **功能**：演示 `completeOnTimeout` 方法，在异步任务超时时返回默认值。
- **流程**：
    - `queryCache()` 模拟缓存查询，固定延迟 1.5 秒。
    - 调用 `completeOnTimeout("缓存降级值", 1, TimeUnit.SECONDS)`，若 1 秒内未完成则返回降级值。
    - 最终输出降级值（因为 1.5 秒 > 1 秒）。

---

### 4. `OrTimeoutDemo.java` – 接口调用超时控制
- **功能**：演示 `orTimeout` 方法，超时后抛出异常并通过 `exceptionally` 降级。
- **流程**：
    - `callRemoteApi()` 模拟远程接口调用，延迟 2 秒。
    - 设置 `orTimeout(1秒)`，超时触发 `TimeoutException`。
    - `exceptionally` 捕获异常，输出日志并返回“默认降级结果”。
    - 最终结果即为降级值。

---

### 5. `RetryDemo1.java` – 固定次数重试
- **功能**：递归实现最多 3 次重试，每次失败后延迟 1 秒再试。
- **流程**：
    - `callApi()` 模拟接口调用，有 60% 概率失败（`Math.random() > 0.4`）。
    - `callApiWithRetry(url, retryCount)` 递归调用：
        - 成功则返回结果。
        - 失败且未达最大重试次数 → 延迟 1 秒后递归调用（重试计数 +1）。
        - 达到最大重试次数 → 返回降级结果“接口降级结果”。

---

### 6. `RetryDemo2.java` – 指数退避重试
- **功能**：类似 `RetryDemo1`，但重试间隔采用指数退避策略（`初始延迟 * 2^重试次数`）。
- **流程**：
    - 初始延迟 1000ms。
    - 第 1 次重试等待 1000ms，第 2 次等待 2000ms，第 3 次等待 4000ms（最多重试 3 次）。
    - 达到最大重试次数后返回降级结果。
    - 其余逻辑与 `RetryDemo1` 一致。

---

这些代码展示了如何利用 `CompletableFuture` 的 `supplyAsync`、`join`、`orTimeout`、`completeOnTimeout`、`exceptionallyCompose` 以及自定义线程池和延迟执行器来构建健壮的异步业务流程。