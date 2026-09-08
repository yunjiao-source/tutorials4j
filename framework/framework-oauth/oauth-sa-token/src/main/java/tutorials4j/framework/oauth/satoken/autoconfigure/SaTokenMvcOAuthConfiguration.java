package tutorials4j.framework.oauth.satoken.autoconfigure;

import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(SaTokenOAuthProperties.class)
@RequiredArgsConstructor
public class SaTokenMvcOAuthConfiguration implements WebMvcConfigurer {
  private final SaTokenOAuthProperties properties;

  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-SA-TOKEN] Sa-Token Mvc Configuration");
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    SaInterceptor saInterceptor = new SaInterceptor();

    InterceptorRegistration registration = registry.addInterceptor(saInterceptor);
    HandlerInterceptorOptions options = properties.getInterceptor();
    registration.excludePathPatterns(options.getExcludePathPatterns());
    registration.addPathPatterns(options.getIncludePathPatterns());

    log.trace("[WEB-SECURITY] 'SaInterceptor' configuration parameters are {}", options);
  }
}
