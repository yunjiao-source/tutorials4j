package tutorials4j.framework.data.hibernate.spi;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.data.hibernate.properties.HibernateDataProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class CacheRegionHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {
  private final HibernateDataProperties properties;

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(
        SimpleAvailableSettings.CACHE_TYPE, properties.getSecondLevelCacheType());
  }
}
