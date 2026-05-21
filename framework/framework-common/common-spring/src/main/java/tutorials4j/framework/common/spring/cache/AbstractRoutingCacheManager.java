package tutorials4j.framework.common.spring.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * 抽象路由缓存管理器，根据动态查找键路由到不同的目标 {@link CacheManager} 实例。
 *
 * <p>类似于 Spring 的 AbstractRoutingDataSource，此类允许在运行时动态选择底层缓存管理器， 适用于多租户、环境隔离等场景。
 *
 * @param <T> 目标缓存管理器的具体类型，必须继承自 {@link CacheManager}
 * @author Yun Jiao
 */
public abstract class AbstractRoutingCacheManager<T extends CacheManager> implements CacheManager {
  private final Map<Object, T> targetCacheManagers = new ConcurrentHashMap<>();

  /**
   * 根据缓存名称获取对应的 {@link Cache} 实例。
   *
   * <p>内部通过 {@link #determineTargetDataSource()} 确定当前目标缓存管理器， 然后委托给该管理器的 {@code getCache} 方法。
   *
   * @param name 缓存名称（不能为 {@code null}）
   * @return 对应的 Cache 实例，如果目标管理器不存在则可能返回 {@code null}
   * @throws IllegalStateException 如果无法确定当前查找键或对应管理器创建失败
   */
  @Override
  public Cache getCache(String name) {
    return this.determineTargetDataSource().getCache(name);
  }

  @Override
  public Collection<String> getCacheNames() {
    return this.determineTargetDataSource().getCacheNames();
  }

  /**
   * 确定当前要使用的目标缓存管理器。
   *
   * <p>首先调用 {@link #determineCurrentLookupKey()} 获取查找键， 然后从内部映射中获取对应的管理器；如果不存在则通过 {@link
   * #createCacheManager(Object)} 动态创建并缓存。
   *
   * @return 当前线程适用的目标缓存管理器
   */
  protected CacheManager determineTargetDataSource() {
    Object lookupKey = this.determineCurrentLookupKey();
    return this.targetCacheManagers.computeIfAbsent(lookupKey, this::createCacheManager);
  }

  /**
   * 确定当前线程的查找键，用于选择目标缓存管理器。
   *
   * <p>实现类通常从 {@link ThreadLocal} 或类似上下文中获取租户 ID、环境标识等。
   *
   * @return 查找键
   */
  protected abstract Object determineCurrentLookupKey();

  /**
   * 当查找键对应的目标缓存管理器不存在时，调用此方法动态创建一个新实例。
   *
   * @param name 查找键（用于标识新管理器的来源或配置）
   * @return 新创建的目标缓存管理器
   */
  protected abstract T createCacheManager(Object name);
}
