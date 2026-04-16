package tutorials4j.framework.data.core.tenant;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TenantProperties.class)
@RequiredArgsConstructor
public class TenantConfiguration implements WebMvcConfigurer {
    private final TenantProperties properties;

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Tenant Configuration");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantHandlerInterceptor()).addPathPatterns(properties.getPathPatterns());
    }

}
