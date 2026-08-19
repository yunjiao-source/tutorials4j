package tutorials4j.framework.cache.redis.customizer;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 命名缓存管理器定制器。
 *
 * <p>该类实现 {@link CacheManagerCustomizer} 接口，用于在 {@link RedisCacheManager} 初始化完成后， 显式调用其 {@link
 * RedisCacheManager#initializeCaches()} 方法。
 *
 * <p><b>背景说明：</b><br>
 * 虽然 {@link RedisCacheManager} 实现了 {@link org.springframework.beans.factory.InitializingBean} 接口，
 * 但在某些场景下（如通过 Spring Boot 自动配置创建的实例），{@code afterPropertiesSet} 方法并不会自动执行，
 * 导致在运行时通过缓存名称动态获取缓存时，可能使用默认配置而非预定义的命名缓存配置。 因此需要手动触发缓存的初始化。
 *
 * @author Yun Jiao
 * @see CacheManagerCustomizer
 * @see RedisCacheManager
 */
public class NamedCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
  /**
   * 对 {@link RedisCacheManager} 进行定制。
   *
   * <p>调用 {@link RedisCacheManager#initializeCaches()} 方法， 强制初始化所有预定义的命名缓存（如果有配置），
   * 确保命名缓存使用预定义的配置，而不是在运行时动态获取缓存时回退到默认配置。
   *
   * @param cacheManager 要定制的 {@link RedisCacheManager} 实例
   */
  @Override
  public void customize(RedisCacheManager cacheManager) {
    // 调用初始化方法
    cacheManager.initializeCaches();
  }
}
