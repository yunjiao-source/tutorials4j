package tutorials4j.feature.basic.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.service.registry.ImportHttpServices;
import tutorials4j.feature.basic.api.BasicApiClient;
import tutorials4j.feature.core.consts.HttpServicesConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@ImportHttpServices(group = HttpServicesConsts.GROUP_BASIC, types = BasicApiClient.class)
public class ApiBasicFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-BASIC-API] Api Basic Feature Auto Configuration");
  }
}
