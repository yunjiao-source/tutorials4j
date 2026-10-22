package tutorials4j.service.system.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SystemServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-SYSTEM] System Service Configuration");
  }
}
