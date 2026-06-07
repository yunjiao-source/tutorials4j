package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.controller"})
public class ControllerFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-Controller] Controller Feature Configuration");
  }
}
