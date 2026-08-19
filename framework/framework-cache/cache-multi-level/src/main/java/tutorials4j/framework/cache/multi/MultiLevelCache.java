package tutorials4j.framework.cache.multi;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

/**
 * 多级缓存实现，组合本地缓存（如 Caffeine）和远程缓存（如 Redis）。
 *
 * <p>缓存操作遵循以下策略：
 *
 * <ul>
 *   <li><b>读取（get）</b>：先查本地缓存，命中则直接返回；未命中则查远程缓存，若远程命中则回填本地缓存后返回。
 *   <li><b>写入（put）</b>：同时写入本地和远程缓存。
 *   <li><b>删除（evict）</b>：同时删除本地和远程缓存。
 *   <li><b>清空（clear）</b>：同时清空本地和远程缓存。
 * </ul>
 *
 * @author Yun Jiao
 * @see Cache
 */
@RequiredArgsConstructor
public class MultiLevelCache implements Cache {
  /** 用于防止缓存击穿的按 Key 粒度的锁集合。 */
  protected final ConcurrentMap<Object, ReentrantLock> locks = new ConcurrentHashMap<>();

  /** 本地缓存（如 Caffeine）。 */
  protected final Cache local;

  /** 远程缓存（如 Redis）。 */
  protected final Cache remote;

  /**
   * 返回缓存名称，委托给本地缓存。
   *
   * @return 缓存名称
   */
  @Override
  public String getName() {
    return local.getName();
  }

  /**
   * 返回原生缓存对象，此处返回当前 MultiLevelCache 实例本身。
   *
   * @return 当前缓存实例
   */
  @Override
  public Object getNativeCache() {
    return this;
  }

  /**
   * 根据键获取缓存值的包装器。
   *
   * <p>查找顺序：本地缓存 → 远程缓存（并回填本地）→ 返回 null。
   *
   * @param key 缓存键
   * @return 包含值的 {@link ValueWrapper}，若不存在则返回 {@code null}
   */
  @Override
  public ValueWrapper get(Object key) {
    // 1. 先查本地
    ValueWrapper wrapper = local.get(key);
    if (wrapper != null) {
      return wrapper;
    }
    // 2. 查远程
    wrapper = remote.get(key);
    if (wrapper != null) {
      // 3. 回填本地
      local.put(key, wrapper.get());
      return wrapper;
    }
    return null;
  }

  /**
   * 根据键获取指定类型的缓存值。
   *
   * <p>查找顺序：本地缓存 → 远程缓存（并回填本地）→ 返回 null。
   *
   * @param key 缓存键
   * @param type 期望的值类型
   * @param <T> 值类型
   * @return 缓存值，若不存在或类型不匹配则返回 {@code null}
   */
  @Override
  public <T> T get(Object key, Class<T> type) {
    T value = local.get(key, type);
    if (value != null) {
      return value;
    }
    value = remote.get(key, type);
    if (value != null) {
      local.put(key, value);
    }
    return value;
  }

  /**
   * 根据键获取缓存值，若不存在则使用 {@code valueLoader} 加载并存入缓存。
   *
   * <p>查找顺序：本地缓存 → 远程缓存（并回填本地）→ 若均不存在则调用 valueLoader 加载并写入两级缓存。
   *
   * @param key 缓存键
   * @param valueLoader 值加载器，用于在缓存未命中时生成值
   * @param <T> 值类型
   * @return 缓存值（可能由 valueLoader 加载）
   * @throws ValueRetrievalException 若 valueLoader 执行失败
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Object key, Callable<T> valueLoader) {
    // 1. 查本地
    Cache.ValueWrapper localWrapper = local.get(key);
    if (localWrapper != null) {
      return (T) localWrapper.get();
    }

    // 2. 查远程
    Cache.ValueWrapper remoteWrapper = remote.get(key);
    if (remoteWrapper != null) {
      Object value = remoteWrapper.get();
      local.put(key, value);
      return (T) value;
    }

    ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
    lock.lock();
    try {
      // 双重检查
      localWrapper = local.get(key);
      if (localWrapper != null) {
        return (T) localWrapper.get();
      }
      remoteWrapper = remote.get(key);
      if (remoteWrapper != null) {
        Object value = remoteWrapper.get();
        local.put(key, value);
        return (T) value;
      }

      // 真正加载
      T value = remote.get(key, valueLoader);
      local.put(key, value);
      return value;
    } finally {
      lock.unlock();
      locks.remove(key);
    }
  }

  /**
   * 将键值对同时写入本地缓存和远程缓存。
   *
   * @param key 缓存键
   * @param value 缓存值
   */
  @Override
  public void put(Object key, Object value) {
    local.put(key, value);
    remote.put(key, value);
  }

  /**
   * 同时从本地缓存和远程缓存中删除指定键对应的条目。
   *
   * @param key 缓存键
   */
  @Override
  public void evict(Object key) {
    local.evict(key);
    remote.evict(key);
  }

  /** 同时清空本地缓存和远程缓存中的所有条目。 */
  @Override
  public void clear() {
    local.clear();
    remote.clear();
  }
}
