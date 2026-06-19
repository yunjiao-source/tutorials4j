package tutorials4j.framework.data.hibernate.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.cache.core.support.CacheType;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_HIBERNATE)
public class HibernateDataProperties {
  private CacheType SecondLevelCacheType = CacheType.MULTI_LEVEL;
}
