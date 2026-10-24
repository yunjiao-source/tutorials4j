package tutorials4j.toolkit.http.service.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import tutorials4j.toolkit.http.service.support.HeaderAppendClientHttpRequestInterceptor;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class ServiceHttpToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-HTTP-SERVICE] Service Http Toolkit Auto Configuration");
  }

  @Bean
  RestClientHttpServiceGroupConfigurer toolkitRestClientHttpServiceGroupConfigurer() {
    log.trace("[TOOLKIT-HTTP-SERVICE] Toolkit Rest Client Http Service Group Configurer");
    HeaderAppendClientHttpRequestInterceptor interceptor =
        new HeaderAppendClientHttpRequestInterceptor();
    return groups ->
        groups.forEachClient(
            (group, builder) -> {
              builder.requestInterceptor(interceptor);
            });
  }
}
