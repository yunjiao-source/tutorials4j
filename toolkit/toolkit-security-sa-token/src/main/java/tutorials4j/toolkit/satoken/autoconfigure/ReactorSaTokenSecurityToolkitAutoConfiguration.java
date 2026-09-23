package tutorials4j.toolkit.satoken.autoconfigure;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.satoken.strategy.AuthFilterPointcutEnum;
import tutorials4j.toolkit.satoken.strategy.CompositeSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.ToolkitSaFilterErrorStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(SaReactorFilter.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class ReactorSaTokenSecurityToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Reactor Sa-Token Security Toolkit Auto Configuration");
  }

  @Bean
  SaReactorFilter saReactorFilter(
      ToolkitSaFilterErrorStrategy toolkitSaFilterErrorStrategy,
      CompositeSaFilterAuthStrategy strategy,
      SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Sa Reactor Filter");
    var options = properties.getFilter();
    return new SaReactorFilter()
        .addInclude(options.getIncludeUrls().toArray(new String[] {}))
        .addExclude(options.getExcludeUrls().toArray(new String[] {}))
        .setAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.auth))
        .setBeforeAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.beforeAuth))
        .setError(toolkitSaFilterErrorStrategy);
  }
}
