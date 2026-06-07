package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.domain.autoconfigure.DomainFeatureConfiguration;

/**
 * 功能模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  DomainFeatureConfiguration.class,
  ControllerFeatureConfiguration.class,
  SignInFeatureConfiguration.class
})
public class FeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE] Feature Auto Configuration");
  }
}
