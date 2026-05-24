package tutorials4j.framework.assy.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.assy.core.properties.ScheduleAssyProperties;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ScheduleAssyProperties.class})
public class AssyConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[ASSY-CORE] Cache Configuration");
  }
}
