package tutorials4j.framework.feature.signin.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.feature.signin.properties.SignInFeatureProperties;
import tutorials4j.framework.feature.signin.service.SignInEndpoint;
import tutorials4j.framework.feature.signin.service.SignInResultHandler;
import tutorials4j.framework.feature.signin.service.SignInTemplateFactory;

/**
 * 签到功能服务模块自动配置类。
 *
 * <p>启用签到功能配置属性，并在缺少自定义 Bean 时自动注册 {@link SignInTemplateFactory} 与 {@link
 * SignInEndpoint}，其中模板工厂会按序应用所有 {@link SignInResultHandler}。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SignInFeatureProperties.class})
public class SignInServiceFeatureConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SIGN-IN] Sign In Service Feature Configuration");
  }

  /**
   * 注册签到端点 Bean。
   *
   * @param signInTemplateFactory 签到模板工厂
   * @return 签到端点实例
   */
  @Bean
  @ConditionalOnMissingBean
  SignInEndpoint signInEndpoint(SignInTemplateFactory signInTemplateFactory) {
    log.trace("[FEATURE-SIGN-IN] Sign In Endpoint");
    return new SignInEndpoint(signInTemplateFactory);
  }

  /**
   * 注册签到模板工厂 Bean。
   *
   * @param properties 签到功能配置属性
   * @param signInResultHandlers 可选的签到结果处理器列表
   * @return 包含全部结果处理器的签到模板工厂实例
   */
  @Bean
  @ConditionalOnMissingBean
  SignInTemplateFactory signInService(
      SignInFeatureProperties properties,
      ObjectProvider<SignInResultHandler> signInResultHandlers) {
    log.trace("[FEATURE-SIGN-IN] Sign In Template Factory");
    return new SignInTemplateFactory(signInResultHandlers.stream().sorted().toList(), properties);
  }
}
