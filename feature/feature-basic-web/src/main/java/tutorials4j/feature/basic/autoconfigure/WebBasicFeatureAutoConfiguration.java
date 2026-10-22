package tutorials4j.feature.basic.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = {"tutorials4j.feature.basic.controller"})
public class WebBasicFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-BASIC-WEB] Web Basic Feature Auto Configuration");
  }
}
