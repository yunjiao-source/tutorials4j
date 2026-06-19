package tutorials4j.framework.cache.core.support;

import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.REDIS;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_MULTI_LEVEL;

import lombok.Getter;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum CacheType implements BaseEnum<Integer> {
  LOCAL(1, "本地缓存", new CacheManagerCreatorCategory[] {TENANT_CAFFEINE, CAFFEINE}),
  REMOTE(2, "远程缓存", new CacheManagerCreatorCategory[] {REDIS}),
  MULTI_LEVEL(
      3,
      "多级缓存",
      new CacheManagerCreatorCategory[] {
        TENANT_MULTI_LEVEL, CacheManagerCreatorCategory.MULTI_LEVEL
      });

  private final int code;
  private final String label;

  @Getter private final CacheManagerCreatorCategory[] creatorCategories;

  CacheType(int code, String label, CacheManagerCreatorCategory[] creatorCategories) {
    this.code = code;
    this.label = label;
    this.creatorCategories = creatorCategories;
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
