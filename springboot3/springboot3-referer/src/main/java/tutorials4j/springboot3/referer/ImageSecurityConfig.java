package tutorials4j.springboot3.referer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
public class ImageSecurityConfig {

    @Value("${allowedOrigins:http://localhost:8080}")
    private List<String> allowedOrigins;

    @Value("${allowedIps:0:0:0:0:0:0:0:1}")
    private List<String> allowedIps;

    @Bean
    public FilterRegistrationBean<ImageSecurityFilter> imageSecurityFilter() {
        FilterRegistrationBean<ImageSecurityFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ImageSecurityFilter(allowedOrigins, allowedIps));
        registrationBean.addUrlPatterns("/images/*");
        return registrationBean;
    }
}
