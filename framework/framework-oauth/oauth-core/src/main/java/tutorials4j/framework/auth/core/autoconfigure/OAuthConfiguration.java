package tutorials4j.framework.auth.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.auth.core.apikey.ApiKeyRateLimiterService;
import tutorials4j.framework.cache.redis.script.RedisScriptExecutor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OAuthProperties.class})
public class OAuthConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-CORE] OAuth Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  ApiKeyRateLimiterService apiKeyRateLimiterService(
      RedisScriptExecutor redisScriptExecutor, OAuthProperties properties) {
    log.trace("[OAUTH-CORE] Api Key Rate Limiter Service");
    return new ApiKeyRateLimiterService(redisScriptExecutor, properties.getApiKeyRateLimiter());
  }
}
