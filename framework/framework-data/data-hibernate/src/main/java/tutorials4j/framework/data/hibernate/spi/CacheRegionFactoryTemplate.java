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
 * TODO
 *
 * @author Yun Jiao
 */
public class CacheRegionFactoryTemplate extends RegionFactoryTemplate {
  private CacheType cacheType = CacheType.MULTI_LEVEL;
  private String regionPrefix = "";

  @Override
  protected StorageAccess createTimestampsRegionStorageAccess(
      String regionName, SessionFactoryImplementor sessionFactory) {
    return registerNewStorageAccess(regionName);
  }

  @Override
  protected DomainDataStorageAccess createDomainDataStorageAccess(
      DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
    return registerNewStorageAccess(regionConfig.getRegionName());
  }

  @Override
  protected StorageAccess createQueryResultsRegionStorageAccess(
      String regionName, SessionFactoryImplementor sessionFactory) {
    return registerNewStorageAccess(regionName);
  }

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

  @Override
  protected void releaseFromUse() {}

  private DomainDataStorageAccess registerNewStorageAccess(final String regionName) {
    return new CacheDomainDataStorageAccess(regionName, cacheType);
  }
}
