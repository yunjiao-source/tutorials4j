package tutorials4j.framework.cache.redisson;

import lombok.RequiredArgsConstructor;
import org.redisson.config.NameMapper;
import tutorials4j.framework.cache.core.CacheNamePrefix;

/**
 * Redisson Key 名称映射器的实现，用于统一为所有 Redis Key 添加租户前缀。
 *
 * <p>通过委托给 {@link CacheNamePrefix#tenant()} 策略，在写入/读取 Redis 时自动为每个 Key 添加当前租户 ID，
 * 并能在读取时正确剥离前缀，实现多租户数据隔离。
 *
 * <p>该实现通常与 Redisson 配置中的 {@code nameMapper} 配合使用， 使得业务代码中使用的原始 Key 名（如锁的 Key、对象的 Key）自动获得租户隔离能力。
 *
 * @author Yun Jiao
 * @see CacheNamePrefix#tenant()
 * @see org.redisson.config.Config#setNameMapper(NameMapper)
 */
@RequiredArgsConstructor
public class PrefixNameMapper implements NameMapper {
  private final String cacheName;

  @Override
  public String map(String s) {
    return CacheNamePrefix.tenant().end().compute(cacheName) + s;
  }

  @Override
  public String unmap(String s) {
    return s;
  }
}
