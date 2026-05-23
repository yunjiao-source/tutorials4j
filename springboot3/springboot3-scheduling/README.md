# springboot3-remind-task

该代码实现了一个灵活的定时任务调度与管理框架，主要功能如下：

## 1. 动态配置管理
- **配置抽象**：`ConfigRepository` 接口定义任务的配置获取、更新、监听等能力。
- **双实现**：
    - `PropertiesConfigRepository`：从 `tasks.properties` 读取配置，支持**文件热更新**（文件监听 + 定期轮询），并可将修改持久化回文件。
    - `YamlConfigRepository`：从 `application.yml` 的 `scheduled.tasks` 读取配置，支持运行时内存动态修改（但不持久化到YAML文件）。
- **工厂模式**：`ConfigRepositoryFactory` 根据系统属性 `scheduled.config.source` 自动选择激活哪个实现，作为主Bean注入。

## 2. 动态任务调度
- **`DynamicCronTask`**：核心调度器。
    - 启动时从配置仓库加载所有启用的 cron 任务，通过 `TaskScheduler` 动态注册。
    - 监听配置变更事件（cron表达式、启用/禁用），自动取消旧任务并重新调度，实现**零重启更新**。
    - 支持任务执行日志和配置元数据读取。

## 3. 高级任务调度示例
- **分布式锁任务**：`DistributedScheduledTask` 演示使用 Redis 锁保证多实例下仅一个执行（需补充锁实现）。
- **间隔动态调整**：`DynamicScheduledTask` 支持运行时修改固定延迟/频率任务的间隔。
- **暂停/恢复**：`PausableScheduledTask` 通过原子布尔标志控制任务执行，并避免并发冲突。
- **任务链**：`TaskChainManager` 支持定义依赖关系的任务链，前一个任务完成后调度下一个。
- **通用调度器**：`UniversalTaskScheduler` 支持自定义触发策略（如指数退避重试）。
- **注解示例**：`ScheduledTasks` 展示 `@Scheduled` 的基本用法。

## 4. 生命周期与资源管理
- 各任务组件在 `@PreDestroy` 中取消所有 `ScheduledFuture`，避免资源泄漏。
- 文件监听服务在销毁时关闭 `WatchService`。

## 整体价值
该框架适用于需要**运行时动态调整定时任务配置**的场景（如数据同步、报表生成），无需重启应用即可修改任务的 cron 表达式或启用/禁用状态，同时提供了分布式协调、任务链、重试策略等扩展能力。