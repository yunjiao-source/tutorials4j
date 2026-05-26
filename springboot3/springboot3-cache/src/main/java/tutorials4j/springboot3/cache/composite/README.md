# composite

代码实现了一个**多级（组合）缓存**的配置，主要功能如下：

1. **本地缓存（Caffeine）**
    - 只针对缓存名 `users`。
    - 配置了初始容量 100、最大容量 500、访问后过期时间 10 秒。

2. **分布式缓存（Redis）**
    - 所有缓存使用统一的前缀（通过 `RedisUtils.tutorials4jCacheKeyPrefix()` 生成）。
    - 默认 TTL 为 Redis 默认配置（未显式设置全局 TTL）。
    - 对 `orders` 缓存单独指定 TTL = 10 秒。
    - 显式调用 `afterPropertiesSet()` 确保 `orders` 的自定义 TTL 配置立即生效，避免运行时使用默认配置。

3. **组合缓存管理器（CompositeCacheManager）**
    - 将 Caffeine 和 Redis 两个缓存管理器按顺序组合。
    - 实际查找缓存时，会**依次**在 Caffeine、Redis 中查找，先命中则返回，实现**多级缓存**的效果。

简单总结：该配置让应用同时拥有本地（Caffeine）和远程（Redis）组合缓存，对不同业务（users/orders）可以分别控制缓存策略，提升性能的同时保证分布式环境下的缓存共享。

**注意**：代码没有实现两级缓存功能