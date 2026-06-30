package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.signin.autoconfigure.SignInServiceFeatureConfiguration;

/**
 * 功能模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({SignInServiceFeatureConfiguration.class})
public class FeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE] Feature Auto Configuration");
  }
}
