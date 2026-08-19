package tutorials4j.framework.cache.core.support;

import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.REDIS;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_CAFFEINE;
import static tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory.TENANT_MULTI_LEVEL;

import lombok.Getter;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * 缓存类型枚举，定义本地、远程与多级缓存，并关联各自的缓存管理器创建器分类。
 *
 * @author Yun Jiao
 */
public enum CacheType implements BaseEnum<Integer> {
  /** 本地缓存，对应 Caffeine 缓存创建器 */
  LOCAL(1, "本地缓存", new CacheManagerCreatorCategory[] {TENANT_CAFFEINE, CAFFEINE}),
  /** 远程缓存，对应 Redis 缓存创建器 */
  REMOTE(2, "远程缓存", new CacheManagerCreatorCategory[] {REDIS}),
  /** 多级缓存（Caffeine + Redis） */
  MULTI_LEVEL(
      3,
      "多级缓存",
      new CacheManagerCreatorCategory[] {
        TENANT_MULTI_LEVEL, CacheManagerCreatorCategory.MULTI_LEVEL
      });

  /** 缓存类型编码 */
  private final int code;

  /** 缓存类型描述 */
  private final String label;

  /** 关联的缓存管理器创建器分类列表 */
  @Getter private final CacheManagerCreatorCategory[] creatorCategories;

  CacheType(int code, String label, CacheManagerCreatorCategory[] creatorCategories) {
    this.code = code;
    this.label = label;
    this.creatorCategories = creatorCategories;
  }

  /** 获取缓存类型编码。 */
  @Override
  public Integer getCode() {
    return code;
  }

  /** 获取缓存类型名称。 */
  @Override
  public String getName() {
    return name();
  }

  /** 获取缓存类型描述。 */
  @Override
  public String getLabel() {
    return label;
  }
}
