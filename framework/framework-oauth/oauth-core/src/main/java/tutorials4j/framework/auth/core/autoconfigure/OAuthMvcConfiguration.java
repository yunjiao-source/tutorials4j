package tutorials4j.framework.auth.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.auth.core.apikey.ApiKeyRateLimiterInterceptor;
import tutorials4j.framework.auth.core.apikey.ApiKeyRateLimiterService;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class OAuthMvcConfiguration implements WebMvcConfigurer {
  private final ApiKeyRateLimiterService apiKeyRateLimiterService;
  private final OAuthProperties properties;

  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-CORE] OAuth MVC Configuration");
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    ApiKeyRateLimiterInterceptor apiKeyRateLimiterInterceptor =
        new ApiKeyRateLimiterInterceptor(apiKeyRateLimiterService);

    InterceptorRegistration registration = registry.addInterceptor(apiKeyRateLimiterInterceptor);
    HandlerInterceptorOptions options = properties.getApiKeyRateLimiter().getInterceptor();
    registration.excludePathPatterns(options.getExcludePathPatterns());
    registration.addPathPatterns(options.getIncludePathPatterns());

    log.trace(
        "[OAUTH-CORE] 'ApiKeyRateLimiterInterceptor' configuration parameters are {}", options);
  }
}
