package tutorials4j.framework.data.hibernate.spi;

import java.util.Map;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import tutorials4j.framework.cache.core.support.CacheType;

/**
 * Hibernate 二级缓存区域工厂的模板实现。
 *
 * <p>为时间戳区域、领域数据区域和查询结果区域统一创建基于 Spring Cache 的存储访问对象， 并支持通过配置项指定缓存类型与区域前缀。
 *
 * @author Yun Jiao
 */
public class CacheRegionFactoryTemplate extends RegionFactoryTemplate {
  /** 二级缓存类型，默认多级缓存。 */
  private CacheType cacheType = CacheType.MULTI_LEVEL;

  /** 缓存区域名称前缀，默认为空。 */
  private String regionPrefix = "";

  /**
   * 创建时间戳区域的存储访问对象。
   *
   * @param regionName 区域名称
   * @param sessionFactory Hibernate 会话工厂
   * @return 时间戳区域的存储访问对象
   */
  @Override
  protected StorageAccess createTimestampsRegionStorageAccess(
      String regionName, SessionFactoryImplementor sessionFactory) {
    return registerNewStorageAccess(regionName);
  }

  /**
   * 创建领域数据区域的存储访问对象。
   *
   * @param regionConfig 领域数据区域配置
   * @param buildingContext 领域数据区域构建上下文
   * @return 领域数据区域的存储访问对象
   */
  @Override
  protected DomainDataStorageAccess createDomainDataStorageAccess(
      DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
    return registerNewStorageAccess(regionConfig.getRegionName());
  }

  /**
   * 创建查询结果区域的存储访问对象。
   *
   * @param regionName 区域名称
   * @param sessionFactory Hibernate 会话工厂
   * @return 查询结果区域的存储访问对象
   */
  @Override
  protected StorageAccess createQueryResultsRegionStorageAccess(
      String regionName, SessionFactoryImplementor sessionFactory) {
    return registerNewStorageAccess(regionName);
  }

  /**
   * 从配置中读取缓存类型与区域前缀。
   *
   * @param settings 会话工厂配置
   * @param configValues 配置键值对
   */
  @Override
  protected void prepareForUse(SessionFactoryOptions settings, Map<String, Object> configValues) {
    Object o = configValues.get(SimpleAvailableSettings.CACHE_TYPE);
    if (o != null) {
      cacheType = (CacheType) o;
    }

    o = configValues.get(SimpleAvailableSettings.CACHE_REGION_PREFIX);
    if (o != null) {
      regionPrefix = (String) o;
    }
  }

  /** 释放资源，无实际清理逻辑。 */
  @Override
  protected void releaseFromUse() {}

  /**
   * 注册并返回指定区域的新存储访问对象。
   *
   * @param regionName 区域名称
   * @return 领域数据存储访问对象
   */
  private DomainDataStorageAccess registerNewStorageAccess(final String regionName) {
    return new CacheDomainDataStorageAccess(regionName, cacheType);
  }
}
