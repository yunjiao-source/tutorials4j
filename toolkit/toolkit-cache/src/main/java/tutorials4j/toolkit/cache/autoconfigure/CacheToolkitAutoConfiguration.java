package tutorials4j.toolkit.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.cache.component.CacheManagerCreator;
import tutorials4j.toolkit.cache.component.CacheManagerFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({CacheToolkitProperties.class})
public class CacheToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-CACHE] Cache Toolkit Auto Configuration");
  }

  @Bean
  CacheManagerFactory cacheManagerFactory(ObjectProvider<CacheManagerCreator<?>> creators) {
    log.trace("[TOOLKIT-CACHE] Cache Manager Factory");
    CacheManagerFactory.instance.setCreators(creators.orderedStream().toList());
    return CacheManagerFactory.instance;
  }
}
