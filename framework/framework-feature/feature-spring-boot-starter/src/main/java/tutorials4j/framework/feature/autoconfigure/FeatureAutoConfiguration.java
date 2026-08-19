package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.signin.autoconfigure.SignInServiceFeatureConfiguration;

/**
 * 功能模块自动配置入口类。
 *
 * <p>汇总导入各功能子模块（如签到）的自动配置，供 Spring Boot 自动装配加载。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({SignInServiceFeatureConfiguration.class})
public class FeatureAutoConfiguration {
  /** 初始化：输出功能模块自动配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE] Feature Auto Configuration");
  }
}
