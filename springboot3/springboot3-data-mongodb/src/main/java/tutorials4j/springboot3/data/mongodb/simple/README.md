# simple

该代码实现了一个基于 **Spring Boot** + **MongoDB** 的简单博客系统，提供了文章（Post）和评论（Comment）的 RESTful API。

## 一、实体层（Model）

### `Post.java`
- 映射 MongoDB 集合 `posts`
- 字段：
  - `id`：主键，由 MongoDB 自动生成
  - `title`：文章标题
  - `content`：文章内容
  - `author`：作者
  - `comments`：评论列表（嵌套文档，类型为 `List<Comment>`）
  - `createdAt`：创建时间（自动设置当前时间）
  - `updatedAt`：更新时间
- 使用 Lombok 简化代码（`@Data`, `@NoArgsConstructor`）

### `Comment.java`
- 嵌套文档，无独立集合
- 字段：
  - `content`：评论内容
  - `author`：评论作者
  - `createdAt`：评论创建时间（默认当前时间）
- 使用 `@Field("created_at")` 指定 MongoDB 字段名

## 二、仓库层（Repository）

### `PostRepository` 继承 `MongoRepository<Post, String>`
提供以下自定义方法：
- `findByAuthor(String author)`：根据作者精确查询文章列表
- `findByTitleContainingIgnoreCase(String keyword)`：根据标题模糊搜索（忽略大小写）
- `@Query("{ 'comments.author': ?0 }")`：使用 MongoDB JSON 查询，查找 **评论中包含特定作者** 的所有文章

## 三、服务层（Service）

### `PostService`
包含业务逻辑：

| 方法 | 功能 |
|------|------|
| `createPost` | 设置创建/更新时间，保存文章 |
| `getAllPosts` | 查询所有文章 |
| `getPostById` | 根据 ID 查询文章，返回 `Optional` |
| `updatePost` | 根据 ID 更新文章标题、内容、作者和更新时间，若不存在则抛异常 |
| `deletePost` | 根据 ID 删除文章 |
| `addComment` | 给指定文章添加评论，自动设置评论时间，更新文章的更新时间 |
| `getPostsByAuthor` | 调用 Repository 按作者查询 |
| `searchPostsByTitle` | 调用 Repository 按标题关键词搜索 |
| `getPostsByCommentAuthor` | 调用 Repository 查询包含某评论作者的所有文章 |

## 四、控制器层（Controller）

### `PostController`
暴露 REST 接口，路径前缀 `/posts`。

| HTTP 方法 | 路径 | 功能 |
|-----------|------|------|
| POST | `/posts` | 创建文章，返回 201 Created |
| GET | `/posts` | 获取所有文章 |
| GET | `/posts/{id}` | 根据 ID 获取文章，不存在返回 404 |
| PUT | `/posts/{id}` | 更新文章（全量更新），不存在返回 404 |
| DELETE | `/posts/{id}` | 删除文章，返回 204 No Content |
| POST | `/posts/{postId}/comments` | 给指定文章添加评论，文章不存在返回 404 |
| GET | `/posts/author/{author}` | 按作者查询文章列表 |
| GET | `/posts/search?keyword=xxx` | 按标题关键词搜索（模糊匹配，忽略大小写） |
| GET | `/posts/comment-author/{commentAuthor}` | 查询包含特定评论作者的所有文章 |

## 五、整体功能总结

这是一个 **基于 MongoDB 的简易博客后端系统**，支持：
- 文章的增删改查
- 嵌套评论的添加
- 按作者、标题关键词、评论作者进行灵活查询
- 自动维护时间戳（创建/更新时间）

代码结构清晰，遵循 Spring Boot 标准分层架构（Controller → Service → Repository），使用 MongoDB 的嵌套文档特性存储评论，无需单独维护评论集合。


MongoDB 是一种 **文档型 NoSQL 数据库**，适合以下典型应用场景：

## 1. 内容管理系统（CMS）与博客系统
- 文章、评论、标签等数据天然嵌套，无需多表关联。
- 例如前文代码中的 `Post` 文档内嵌 `Comment` 列表，读写高效且模型直观。

## 2. 实时数据分析与日志处理
- 高写入吞吐量（支持海量日志、点击流）。
- 可动态添加字段，适应不同格式的日志。
- 配合 TTL 索引自动过期旧数据。

## 3. 电子商务（商品目录与属性）
- 商品属性多变（如手机、服装字段不同），MongoDB 灵活 schema 轻松应对。
- 支持嵌套的规格、评论、库存等信息。

## 4. 物联网（IoT）数据
- 设备产生海量时序数据，MongoDB 分片集群可水平扩展。
- 通配符索引方便查询任意动态字段。

## 5. 移动应用（用户配置文件、推送记录）
- JSON 风格文档与客户端数据结构一致，开发效率高。
- 高并发读写，提供地理空间索引（如附近的店铺）。

## 6. 个性化推荐与实时画像
- 用户画像特征可动态增减，单文档更新原子性强。
- 支持数组、嵌套对象，方便存储行为序列。

## 7. 缓存层（替代 Redis 的部分场景）
- 提供持久化，可承受更大数据量。
- 二级索引比 Redis 更丰富。
- 但不如 Redis 内存级速度，通常做持久缓存。

## 8. 元数据存储、配置中心
- 系统配置、字典数据直接存取 JSON 文档，无需 DDL 变更。
- 适合频繁读取、偶尔更新的场景。

## 不适合的场景
- **复杂多表事务**（如银行账务）：MongoDB 4.0+ 支持多文档事务，但性能低于关系库。
- **高度结构化的强关联数据**：如财务 ERP，仍建议用 PostgreSQL。
- **报表与即席分析**：不如列式数据库（ClickHouse）或传统 OLAP 引擎。

简言之，**MongoDB 最强场景是数据结构多变、读写量大、需要水平扩展且** **能接受最终一致性或有限事务** **的应用**。