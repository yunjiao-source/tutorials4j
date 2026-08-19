package tutorials4j.framework.tenant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.spring.cache.AbstractRoutingCacheManager;

/**
 * 租户隔离的 Caffeine 缓存管理器。
 *
 * <p>继承 {@link AbstractRoutingCacheManager}，根据当前租户 ID 创建或获取独立的 {@link CaffeineCacheManager}
 * 实例，实现不同租户的缓存数据完全隔离。
 *
 * <p>查找键取自 {@link TenantContextHolder}，每个租户对应一个独立的缓存管理器实例，实例按需创建并被缓存复用。
 *
 * @author Yun Jiao
 * @see AbstractRoutingCacheManager
 * @see CaffeineCacheManagerCreator
 */
@Slf4j
@RequiredArgsConstructor
public class TenantCaffeineCacheManager extends AbstractRoutingCacheManager<CaffeineCacheManager> {
  private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

  /**
   * 确定当前租户的查找键。
   *
   * <p>从 {@link TenantContextHolder} 中获取当前线程绑定的租户标识，作为路由到对应租户缓存管理器的依据。
   *
   * @return 租户 ID（可能为 {@code null}，此时外层将无法确定目标管理器）
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return TenantContextHolder.get();
  }

  /**
   * 为指定租户（查找键）创建一个新的 Caffeine 缓存管理器实例。
   *
   * <p>委托给 {@link CaffeineCacheManagerCreator#newInstance()} 生成默认配置的管理器，创建结果会被父类缓存以便后续复用。
   *
   * @param name 租户标识（查找键）
   * @return 新创建的 Caffeine 缓存管理器
   */
  @Override
  protected CaffeineCacheManager createCacheManager(Object name) {
    if (log.isDebugEnabled()) {
      log.debug("创建Caffeine租户缓存管理器, name = {}", name);
    }

    return caffeineCacheManagerCreator.newInstance();
  }
}
