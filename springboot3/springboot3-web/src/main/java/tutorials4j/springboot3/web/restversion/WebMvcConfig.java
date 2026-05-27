package tutorials4j.springboot3.web.restversion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tutorials4j.springboot3.web.restversion.header.VersionHeaderRequestMappingHandlerMapping;
import tutorials4j.springboot3.web.restversion.path.VersionPathFilter;
import tutorials4j.springboot3.web.restversion.path.VersionPathRequestMappingHandlerMapping;

/**
 * 配置
 *
 * @author yangyunjiao
 */
@Slf4j
@Configuration
public class WebMvcConfig {

  @Configuration
  @ConditionalOnProperty(
      prefix = "rest",
      name = "version",
      havingValue = "header",
      matchIfMissing = true)
  public static class VersionHeaderConfig implements WebMvcRegistrations {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
      log.info("=== VersionHeaderRequestMappingHandlerMapping ===");
      return new VersionHeaderRequestMappingHandlerMapping();
    }
  }

  @Configuration
  @ConditionalOnProperty(prefix = "rest", name = "version", havingValue = "path")
  public static class VersionPathConfig implements WebMvcRegistrations {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
      log.info("=== VersionPathRequestMappingHandlerMapping ===");
      RequestMappingHandlerMapping bean = new VersionPathRequestMappingHandlerMapping();
      bean.setOrder(-1);
      return bean;
    }

    @Bean
    public VersionPathFilter versionPathFilter() {
      return new VersionPathFilter();
    }
  }
}
