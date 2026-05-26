# simple

## 代码功能概述

项目实现了一个**基于 Spring Boot 3 + Spring Batch 的用户数据批处理导入功能**。主要功能如下：

### 1. 数据抽取（Extract）
- 从 CSV 文件（`sample-users.csv`）中读取用户数据。
- 支持自定义分隔符（默认逗号）、跳过标题行、文件不存在时容错处理。
- 映射到中间对象 `UserCsvRecord`（包含 name 和 email）。

### 2. 数据转换与清洗（Transform）
通过组合多个 `ItemProcessor` 按顺序处理每条记录：
- **验证**：使用 `UserCsvValidator` 校验 name 非空且长度在配置范围内（默认 2~30），email 符合正则表达式格式。
- **类型转换**：`TransferItemProcessor` 将 `UserCsvRecord` 转换为 `User` 实体。
- **清洗**：`CleanItemProcessor` 对 name 进行 trim、合并多余空格、移除特殊字符，然后转为大写；对 email 进行 trim 并转为小写。
- **日志记录**：`LogItemProcessor` 在 debug 级别记录正在处理的用户名。

### 3. 数据加载（Load）
- 使用 `JpaItemWriter` 将清洗后的 `User` 对象批量写入数据库（通过 JPA / Hibernate）。
- 数据库连接和表结构自动初始化（`ddl-auto: update`）。

### 4. 批处理作业配置
- 定义了一个完整的 Spring Batch Job（`importUserJob`），包含一个 Step（`importUserStep`）。
- Step 采用 Chunk 处理模式（默认每 100 条提交一次）。
- 支持容错机制：跳过指定异常（默认跳过所有 Exception，但 `ValidationException` 不跳过），重试失败记录（默认重试 3 次）。

### 5. 作业触发方式
提供了三种启动批处理作业的方式：
- **应用启动时自动执行**：`CommandLineRunner` 实现。
- **定时调度**：`@Scheduled` 每 15 秒执行一次。
- **REST API 手动触发**：`POST /import-users` 启动作业，并提供 `GET /status/{jobExecutionId}` 查询作业状态。

### 6. 监控与日志
- **日志监听器**：在 Step 和 Job 执行前后输出详细日志，包括读写数量、跳过数量等。
- **Micrometer 指标监控**：记录作业启动次数、执行时长、读写跳过的记录数，暴露给 `/actuator` 端点（通过 `management` 配置）。
- **日志文件输出**：将批处理日志写入 `logs/batch-processing.log`，支持滚动策略。

### 7. 配置外部化
- 通过 `application.yml` 和 `@ConfigurationProperties` 管理批处理参数：
    - 文件路径、分隔符、跳过行数。
    - 校验规则（name 长度范围、email 正则）。
    - Chunk 大小、跳过限制、重试次数。
    - 数据库连接、JPA、批处理表初始化等。

### 整体流程总结
```
CSV文件 → FlatFileItemReader → 验证处理器 → 转换处理器 → 清洗处理器 → 日志处理器 → JPA写入器 → PostgreSQL数据库
```
同时支持作业执行监控、日志记录和多种触发方式，是一个典型的生产级数据 ETL（Extract-Transform-Load）批处理示例。