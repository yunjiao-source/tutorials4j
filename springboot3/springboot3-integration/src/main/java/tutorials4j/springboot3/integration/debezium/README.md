# debezium

## 代码分析

该项目是一个基于 Spring Boot 3 的简单应用，利用 **Debezium Embedded Engine** 实时捕获 PostgreSQL 中 `public.t_user` 表的变更事件（目前仅关注 `name` 和 `age` 列），并将事件信息打印到日志。以下从配置、核心组件、潜在问题及改进建议四个维度进行分析。

---

### 一、配置分析 (`DebeziumConnectorConfig`)

#### ✅ 正确做法
- 使用 `pgoutput` 逻辑解码插件（PostgreSQL 10+ 推荐）
- 指定 `table.include.list` 限定监听表
- 通过 `column.include.list` 过滤只需要的列
- 设置 `publication.autocreate.mode=filtered` 自动创建所需发布

#### ⚠️ 潜在问题

| 配置项 | 当前值 | 问题说明 |
|--------|--------|----------|
| `offset.storage.file.filename` | `File.createTempFile()` 生成的临时文件 | 每次重启生成新文件，导致偏移量丢失，重启后会从头读取所有历史变更（可能重复处理） |
| `database.history` | `MemoryDatabaseHistory` | 仅内存保存 schema 历史，重启后丢失。若表结构发生过变更（如加列），引擎无法正确解析旧事件的 schema |
| `slot.name` | 固定 `dbz_customerdb_listener` | 多次启动时若 offset 文件丢失，会尝试创建同名槽，可能因已存在而失败；需手动删除或使用 `DROP_REPLICATION_SLOT` |
| `database.server.id` | 固定 `10181` | 同一 PostgreSQL 实例下若有多个 Debezium 连接器，ID 必须唯一，否则冲突 |
| `column.include.list` | `public.t_user.name,public.t_user.age` | 语法正确，但注意列名大小写敏感（此处均为小写，与表定义一致，无问题） |

---

### 二、监听器分析 (`DebeziumListener`)

#### ✅ 正确做法
- 使用 `@PostConstruct` 和 `@PreDestroy` 管理引擎生命周期
- 通过单线程 `Executor` 运行引擎，避免阻塞主线程

#### ⚠️ 潜在问题

1. **异常处理缺失**  
   `debeziumEngine` 运行中若抛出未捕获异常，线程终止后引擎不再工作，且应用无感知。  
   ➜ 应通过 `using(..).notifying(..).using(CompletableFuture)` 或设置 `DebeziumEngine.Builder#using(ErrorHandler)`。

2. **事件处理健壮性不足**
   ```java
   Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
   ```
    - 未检查 `sourceRecord.value()` 是否为 `Struct` 类型（如 tombstone 事件、格式异常时可能为 `null`）
    - 未处理 `Operation` 类型（`READ` / `CREATE` / `UPDATE` / `DELETE`）
    - 强制转换可能抛出 `ClassCastException`

3. **日志输出过重**  
   `log.info("Key = {}, Value = {}", ...)` 和 `log.info("SourceRecordChangeValue = '{}'", sourceRecordRecordChangeEvent)` 会打印大量内容，生产环境应降为 `DEBUG`。

4. **未处理引擎关闭时的优雅终止**  
   `executor` 未在 `stop()` 中关闭，可能导致线程泄漏。建议同时关闭 `executor`。

---

### 三、主类与配置属性

- 主类为标准 Spring Boot 入口，无特殊问题。
- `application.properties` 定义了数据库连接和日志级别，未指定 `customer.datasource.*` 外的必要参数（如 `database.timezone`），但当前表无时间字段，不影响。
- `db.sql` 提供了测试表和数据，结构简单。

---

### 四、改进建议（按优先级排序）

#### 🔴 高优先级（避免数据丢失或应用崩溃）

1. **使用持久化 Offset 存储**  
   改为固定文件路径，例如：
   ```java
   .with("offset.storage.file.filename", "/data/debezium/offsets.dat")
   ```

2. **替换 MemoryDatabaseHistory**  
   推荐使用 `FileDatabaseHistory`（需指定文件路径）或生产级 `KafkaDatabaseHistory`。

3. **添加引擎异常处理器**
   ```java
   .using((success, message, error) -> {
       if (error != null) {
           log.error("Debezium engine failed", error);
           // 可选：重启引擎或发送告警
       }
   })
   ```

4. **安全处理 SourceRecord 值**
   ```java
   Object value = sourceRecord.value();
   if (!(value instanceof Struct)) {
       log.debug("Ignored non-Struct value: {}", value);
       return;
   }
   Struct struct = (Struct) value;
   String op = struct.getString("op");
   // 处理不同操作...
   ```

#### 🟡 中优先级（运维友好性）

5. **动态生成或验证复制槽**  
   首次启动时检查槽是否存在，避免冲突；或在应用停止时删除槽（根据业务需求）。

6. **使用唯一 server.id**  
   可通过环境变量或随机数生成，避免多实例冲突。

7. **关闭时同时停止 Executor**
   ```java
   executor.shutdown();
   try { if (!executor.awaitTermination(5, TimeUnit.SECONDS)) executor.shutdownNow(); } 
   catch (InterruptedException e) { executor.shutdownNow(); }
   ```

#### 🟢 低优先级（代码完善）

8. **提取配置常量**，避免魔法字符串。
9. **启用并完善注释掉的业务处理逻辑**（如将变更发送到消息队列）。
10. **考虑使用 Debezium Spring Boot Starter**，减少样板代码。

---

### 五、总结

该代码实现了一个**可用但脆弱**的 CDC（Change Data Capture）监听器。在开发测试环境下可快速验证功能，但**不适合直接用于生产**。主要短板集中在 **状态持久化**（offset、schema history）和 **异常处理** 两方面。按上述建议改造后，可以构建一个稳定、可恢复的数据库变更捕获服务。

