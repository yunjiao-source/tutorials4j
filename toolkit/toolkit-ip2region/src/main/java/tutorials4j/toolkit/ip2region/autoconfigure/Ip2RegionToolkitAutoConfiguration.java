package tutorials4j.toolkit.ip2region.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.ip2region.component.Ip2RegionSearcher;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  Ip2RegionToolkitProperties.class,
})
public class Ip2RegionToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-IP2REGION] Ip2Region Toolkit Auto Configuration");
  }

  @Bean
  Ip2RegionSearcher ip2RegionSearcher(Ip2RegionToolkitProperties properties) {
    log.trace("[TOOLKIT-IP2REGION] Ip2 Region Searcher");
    return new Ip2RegionSearcher(properties);
  }
}
