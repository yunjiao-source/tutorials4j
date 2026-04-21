package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.web.http.CachedRequestBodyConfiguration;

/**
 * 签名接口配置
 *
 * @author Yun Jiao
 */
@Configuration
@Import({CachedRequestBodyConfiguration.class})
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SignatureInterceptor signatureInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signatureInterceptor)
                .addPathPatterns("/signature/**");
    }
}