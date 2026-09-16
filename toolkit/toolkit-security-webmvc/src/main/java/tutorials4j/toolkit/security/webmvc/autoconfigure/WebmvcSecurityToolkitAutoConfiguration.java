package tutorials4j.toolkit.security.webmvc.autoconfigure;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.toolkit.satoken.autoconfigure.SaTokenSecurityToolkitProperties;
import tutorials4j.toolkit.satoken.func.CompositeSaParamFunction;
import tutorials4j.toolkit.satoken.strategy.AuthFilterPointcutEnum;
import tutorials4j.toolkit.satoken.strategy.CompositeFilterAuthStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class WebmvcSecurityToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SECURITY-WEBMVC] Webmvc Security Toolkit Auto Configuration");
  }

  @Bean
  SaServletFilter saServletFilter(
      CompositeFilterAuthStrategy strategy, SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-WEBMVC] Sa Servlet Filter");
    var options = properties.getFilter();
    return new SaServletFilter()
        .addExclude(options.getIncludeUrls().toArray(new String[] {}))
        .addExclude(options.getExcludeUrls().toArray(new String[] {}))
        .setAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.auth))
        .setBeforeAuth(strategy.copyAndSetPointcut(AuthFilterPointcutEnum.beforeAuth));
  }

  @Configuration
  @RequiredArgsConstructor
  public static class SecurityToolkitWebMvcConfigurer implements WebMvcConfigurer {
    private final SaTokenSecurityToolkitProperties properties;
    private final CompositeSaParamFunction compositeSaParamFunction;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      var options = properties.getInterceptor();
      registry
          .addInterceptor(
              new SaInterceptor(compositeSaParamFunction).isAnnotation(options.isAnnotation()))
          .addPathPatterns(options.getIncludePathPatterns())
          .excludePathPatterns(options.getExcludePathPatterns());
    }
  }
}
