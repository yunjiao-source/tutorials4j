package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final TenantInterceptor tenantInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tenantInterceptor).addPathPatterns("/**");
  }
}
