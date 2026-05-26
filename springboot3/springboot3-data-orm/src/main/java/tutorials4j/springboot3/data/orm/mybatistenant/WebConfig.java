package tutorials4j.springboot3.data.orm.mybatistenant;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器注册 将拦截器注册到 Spring 的拦截器链
 *
 * @author Yun Jiao
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new TenantHandlerInterceptor()).addPathPatterns("/**");
  }
}
