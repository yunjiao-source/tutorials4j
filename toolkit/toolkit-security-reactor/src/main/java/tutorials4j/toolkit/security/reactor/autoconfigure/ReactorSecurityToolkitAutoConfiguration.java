package tutorials4j.toolkit.security.reactor.autoconfigure;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.satoken.autoconfigure.SaTokenSecurityToolkitProperties;
import tutorials4j.toolkit.satoken.strategy.AuthFilterPointcutEnum;
import tutorials4j.toolkit.satoken.strategy.CompositeFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.SimpleSaFilterErrorStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class ReactorSecurityToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SECURITY-REACTOR] Reactor Security Toolkit Auto Configuration");
  }

  @Bean
  SaReactorFilter saReactorFilter(
      SimpleSaFilterErrorStrategy simpleSaFilterErrorStrategy,
      CompositeFilterAuthStrategy strategy,
      SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-REACTOR] Sa Reactor Filter");
    var options = properties.getFilter();
    return new SaReactorFilter()
        .addInclude(options.getIncludeUrls().toArray(new String[] {}))
        .addExclude(options.getExcludeUrls().toArray(new String[] {}))
        .setAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.auth))
        .setBeforeAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.beforeAuth))
        .setError(simpleSaFilterErrorStrategy);
  }
}
