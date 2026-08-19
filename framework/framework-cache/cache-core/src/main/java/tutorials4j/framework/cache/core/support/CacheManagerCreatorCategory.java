package tutorials4j.framework.cache.core.support;

import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * 缓存管理器创建器分类枚举，标识不同的缓存实现类型。
 *
 * @author Yun Jiao
 */
public enum CacheManagerCreatorCategory implements BaseEnum<Integer> {
  /** Caffeine 缓存 */
  CAFFEINE(1, "Caffeine缓存"),
  /** 两级缓存（Caffeine + Redis） */
  MULTI_LEVEL(2, "两级缓存(Caffeine + Redis)"),
  /** Redis 缓存 */
  REDIS(3, "Redis缓存"),
  /** 本地 Caffeine 缓存（租户） */
  TENANT_CAFFEINE(4, "本地Caffeine缓存（租户）"),
  /** 两级缓存（Caffeine + Redis）（租户） */
  TENANT_MULTI_LEVEL(5, "两级缓存(Caffeine + Redis)（租户）");

  /** 枚举编码 */
  private final Integer code;

  /** 枚举描述 */
  private final String label;

  CacheManagerCreatorCategory(Integer code, String label) {
    this.code = code;
    this.label = label;
  }

  /** 获取枚举编码。 */
  @Override
  public Integer getCode() {
    return code;
  }

  /** 获取枚举名称。 */
  @Override
  public String getName() {
    return name();
  }

  /** 获取枚举描述。 */
  @Override
  public String getLabel() {
    return label;
  }
}
