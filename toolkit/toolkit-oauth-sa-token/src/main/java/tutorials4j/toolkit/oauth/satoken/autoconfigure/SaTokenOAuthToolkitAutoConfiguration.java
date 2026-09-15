package tutorials4j.toolkit.oauth.satoken.autoconfigure;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.oauth.satoken.SaLogForSlf4j;
import tutorials4j.toolkit.oauth.satoken.SaTokenHandleException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class SaTokenOAuthToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT - OAUTH - SA TOKEN] Sa-Token OAuth Toolkit Auto Configuration");
  }

  @Bean
  SaLogForSlf4j saLogForSlf4j() {
    log.trace("[TOOLKIT - OAUTH - SA TOKEN] Sa Log For Slf4j");
    return new SaLogForSlf4j();
  }

  @Bean
  SaTokenHandleException saTokenHandleException(Tracer tracer) {
    log.trace("[TOOLKIT - OAUTH - SA TOKEN] Sa Token Handle Exception");
    return new SaTokenHandleException(tracer);
  }
}
