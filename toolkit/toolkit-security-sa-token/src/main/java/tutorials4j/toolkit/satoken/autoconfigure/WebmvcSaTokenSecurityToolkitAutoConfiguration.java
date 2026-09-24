package tutorials4j.toolkit.satoken.autoconfigure;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.toolkit.satoken.func.CompositeSaParamFunction;
import tutorials4j.toolkit.satoken.strategy.CompositeSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.ToolkitSaFilterErrorStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(SaServletFilter.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebmvcSaTokenSecurityToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Webmvc Sa-Token Security Toolkit Auto Configuration");
  }

  @Bean
  SaServletFilter saServletFilter(
      ToolkitSaFilterErrorStrategy toolkitSaFilterErrorStrategy,
      CompositeSaFilterAuthStrategy compositeSaFilterAuthStrategy,
      SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Sa Servlet Filter");
    var options = properties.getFilter();
    return new SaServletFilter()
        .addInclude(options.getIncludeUrls().toArray(new String[] {}))
        .addExclude(options.getExcludeUrls().toArray(new String[] {}))
        .setAuth(compositeSaFilterAuthStrategy.newInstanceBy(options.getAuthStrategies()))
        .setBeforeAuth(
            compositeSaFilterAuthStrategy.newInstanceBy(options.getBeforeAuthStrategies()))
        .setError(toolkitSaFilterErrorStrategy);
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
              new SaInterceptor(compositeSaParamFunction)
                  .isAnnotation(options.isHandleAnnotation()))
          .addPathPatterns(options.getIncludePathPatterns())
          .excludePathPatterns(options.getExcludePathPatterns());
    }
  }
}
