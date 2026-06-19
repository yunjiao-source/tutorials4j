package tutorials4j.framework.cache.core.support;

import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum CacheManagerCreatorCategory implements BaseEnum<Integer> {
  CAFFEINE(1, "Caffeine缓存"),
  MULTI_LEVEL(2, "两级缓存(Caffeine + Redis)"),
  REDIS(3, "Redis缓存"),
  TENANT_CAFFEINE(4, "本地Caffeine缓存（租户）"),
  TENANT_MULTI_LEVEL(5, "两级缓存(Caffeine + Redis)（租户）");

  private final Integer code;
  private final String label;

  CacheManagerCreatorCategory(Integer code, String label) {
    this.code = code;
    this.label = label;
  }

  @Override
  public Integer getCode() {
    return code;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public String getLabel() {
    return label;
  }
}
