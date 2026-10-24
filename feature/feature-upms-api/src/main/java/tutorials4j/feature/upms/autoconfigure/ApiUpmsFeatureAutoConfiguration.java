package tutorials4j.feature.upms.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.service.registry.ImportHttpServices;
import tutorials4j.feature.core.consts.HttpServicesConsts;
import tutorials4j.feature.upms.api.UpmsApiClient;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@ImportHttpServices(group = HttpServicesConsts.GROUP_UPMS, types = UpmsApiClient.class)
public class ApiUpmsFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-UPMS-API] Api UPMS Feature Auto Configuration");
  }
}
