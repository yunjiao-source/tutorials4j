package tutorials4j.springboot3.webflux.tracing.mdc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
public class WebConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .filter(
            (request, next) -> {
              ClientRequest newRequest =
                  ClientRequest.from(request)
                      .headers(
                          headers -> {
                            String traceId = MDC.get(TraceConstants.TRACE_ID);
                            String spanId = MDC.get(TraceConstants.SPAN_ID);

                            if (traceId != null) {
                              headers.set(TraceConstants.TRACE_ID, traceId);
                            }
                            if (spanId != null) {
                              String childSpanId = TraceIdGenerator.generateSpanId();
                              headers.set(TraceConstants.PARENT_SPAN_ID, spanId);
                              headers.set(TraceConstants.SPAN_ID, childSpanId);
                            }
                            log.info("WebClient添加追踪信息");
                          })
                      .build();
              return next.exchange(newRequest);
            })
        .build();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.additionalInterceptors(new TraceRestTemplateInterceptor()).build();
  }
}
