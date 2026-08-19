package tutorials4j.framework.cache.multi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

/**
 * 多级缓存管理器，组合本地缓存管理器（如 Caffeine）和远程缓存管理器（如 Redis）。
 *
 * <p>对于每个缓存名称，会尝试从两个底层管理器中分别获取对应的 {@link Cache} 实例， 然后包装成 {@link MultiLevelCache} 返回。
 *
 * <p>注意：如果一个缓存名称只存在于其中一个管理器，则 {@link #getCache(String)} 会返回 {@code null}， 这保证了多级缓存必须同时具备本地和远程两端的配置。
 *
 * @author Yun Jiao
 * @see AbstractCacheManager
 * @see MultiLevelCache
 */
@RequiredArgsConstructor
public class MultiLevelCacheManager extends AbstractCacheManager {
  private final CacheManager local; // 本地缓存管理器
  private final CacheManager remote; // 远程缓存管理器

  /**
   * 加载所有缓存名称的并集，并返回对应的 {@link MultiLevelCache} 集合。
   *
   * <p>该方法会遍历本地和远程管理器缓存名称的并集，对每个名称调用 {@link #getCache(String)} 获取缓存实例；
   * 仅当名称同时存在于本地与远程管理器中时，才会得到有效的多级缓存实例。
   *
   * @return 所有可用的多级缓存实例集合
   */
  @Override
  protected Collection<? extends Cache> loadCaches() {
    // 合并两个管理器的缓存名称
    Set<String> names = new HashSet<>();
    names.addAll(local.getCacheNames());
    names.addAll(remote.getCacheNames());
    return names.stream().map(this::getCache).collect(Collectors.toList());
  }

  /**
   * 根据缓存名称获取对应的多级缓存实例。
   *
   * <p>只有当本地和远程管理器中都存在该名称的缓存时，才会返回一个有效的 {@link MultiLevelCache}， 否则返回 {@code null}。
   *
   * @param name 缓存名称
   * @return 多级缓存实例，若本地或远程任意一方缺失则返回 {@code null}
   */
  @Override
  public Cache getCache(String name) {
    Cache caffeineCache = local.getCache(name);
    Cache redisCache = remote.getCache(name);
    if (caffeineCache == null || redisCache == null) {
      return null;
    }
    return new MultiLevelCache(caffeineCache, redisCache);
  }
}
