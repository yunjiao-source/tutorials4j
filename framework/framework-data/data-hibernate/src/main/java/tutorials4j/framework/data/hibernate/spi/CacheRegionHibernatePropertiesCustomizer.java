package tutorials4j.framework.data.hibernate.spi;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.data.hibernate.properties.HibernateDataProperties;

/**
 * Hibernate 属性自定义器。
 *
 * <p>将配置的二级缓存类型写入 Hibernate 的配置属性中，供 {@link CacheRegionFactoryTemplate} 读取使用。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class CacheRegionHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {
  private final HibernateDataProperties properties;

  /**
   * 向 Hibernate 属性中写入二级缓存类型。
   *
   * @param hibernateProperties Hibernate 属性集合
   */
  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(
        SimpleAvailableSettings.CACHE_TYPE, properties.getSecondLevelCacheType());
  }
}
