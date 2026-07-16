# [059][调度模块]配置驱动的任务仓库 – 从 YAML 加载任务

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 摘要
为了方便运维和快速配置，框架支持通过 `application.yml` 定义定时任务。`YamlTaskRepository` 将配置解析为 `YamlTask` 对象，并暴露增删改查接口。本文解析配置结构、加载流程及与 `ScheduleTaskManager` 的集成。

## 1. 配置结构
```yaml
tutorials4j:
  schedule:
    default-execution:
      initial-delay: 30s   # 默认初始延迟
      max-failure-count: 3 # 默认最大失败次数
    tasks:
      syncOrderTask:
        class-simple-name: orderSyncRunner
        cron: "0 0/5 * * * ?"
        enabled: true
        description: "同步订单"
        metadata:
          source: "mysql"
        execution:
          max-execution-count: 100
          due-date: "2025-12-31T23:59:59Z"
      # 更多任务...
```
- `TaskOptions` 映射单个任务配置，`TaskExecutionOptions` 可覆盖全局默认值。
- `YamlTask.of(TaskOptions)` 负责转换为领域对象 `YamlTask`。

## 2. YamlTaskRepository 初始化
```java
public YamlTaskRepository(ScheduleProperties properties) {
    properties.getTasks().forEach((name, opts) -> {
        if (!opts.validate()) throw new IllegalArgumentException(...);
        YamlTask task = YamlTask.of(opts);
        task.setName(name);
        taskMap.put(name, task);
    });
}
```
- 构造函数中完成解析，存入 `ConcurrentHashMap`。
- 支持后续运行时增删改（`create`、`update`、`delete`），但默认实现仅做内存操作，可被替换为数据库版本。

## 3. 与调度器的集成
```java
@Configuration
public class ScheduleConfiguration {
    @Bean
    @ConditionalOnMissingBean
    TaskRepository<?> yamlTaskRepository(ScheduleProperties properties) {
        return new YamlTaskRepository(properties);
    }

    @Bean
    ScheduleTaskManager scheduleTaskManager(
            TaskRepository<?> repository,
            ObjectProvider<ChangeStatusEventConsumer> consumers) {
        return new ScheduleTaskManager(repository, consumers.orderedStream().collect(Collectors.toList()));
    }
}
```
- Spring Boot 自动配置将 `YamlTaskRepository` 作为默认 `TaskRepository`。
- `ScheduleTaskManager` 初始化时会调用 `taskRepository.findAll()` 加载所有任务并逐一 `addTask`。

## 4. 运维价值
- **声明式配置**：无需代码修改即可调整任务 cron、开关状态、限流参数。
- **可替换性**：通过 `@ConditionalOnMissingBean` 允许用户自定义 `TaskRepository`（如从数据库或配置中心读取）。