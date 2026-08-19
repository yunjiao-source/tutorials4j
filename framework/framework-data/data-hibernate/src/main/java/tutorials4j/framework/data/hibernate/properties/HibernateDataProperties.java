package tutorials4j.framework.data.hibernate.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.cache.core.support.CacheType;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * Hibernate 数据访问组件配置属性。
 *
 * <p>对应配置前缀 {@value PropertiesConsts#PROPERTY_PREFIX_DATA_HIBERNATE}，用于配置 Hibernate
 * 相关的数据访问参数，例如二级缓存类型。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_HIBERNATE)
public class HibernateDataProperties {
  /** Hibernate 二级缓存类型，默认使用多级缓存（{@link CacheType#MULTI_LEVEL}）。 */
  private CacheType SecondLevelCacheType = CacheType.MULTI_LEVEL;
}
