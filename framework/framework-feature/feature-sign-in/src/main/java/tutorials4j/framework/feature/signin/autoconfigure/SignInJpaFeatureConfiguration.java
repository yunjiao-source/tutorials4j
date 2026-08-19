package tutorials4j.framework.feature.signin.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.feature.signin.jpa.SignInResultEndpoint;
import tutorials4j.framework.feature.signin.jpa.SignInResultService;

/**
 * 签到功能 JPA 模块自动配置类。
 *
 * <p>扫描签到 JPA 相关的实体、Repository 与组件，并在缺少自定义 {@link SignInResultEndpoint} Bean 时，基于 {@link
 * SignInResultService} 自动注册签到结果端点。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
public class SignInJpaFeatureConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SIGN-IN] Sign In Jpa Feature Configuration");
  }

  /**
   * 注册签到结果端点 Bean。
   *
   * @param signInResultService 签到结果服务
   * @return 签到结果端点实例
   */
  @Bean
  @ConditionalOnMissingBean
  SignInResultEndpoint signInResultEndpoint(SignInResultService signInResultService) {
    log.trace("[FEATURE-SIGN-IN] Sign In Result End point");
    return new SignInResultEndpoint(signInResultService);
  }
}
