package tutorials4j.springboot3.mdc;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * RestTemplate 拦截器（服务间调用）
 *
 * @author Yun Jiao
 */
@Slf4j
public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    // 传递追踪信息到下游服务
    String traceId = MDC.get(TraceConstants.TRACE_ID);
    String spanId = MDC.get(TraceConstants.SPAN_ID);

    if (traceId != null) {
      request.getHeaders().add(TraceConstants.TRACE_ID, traceId);
    }
    if (spanId != null) {
      // 生成新的子span
      String childSpanId = TraceIdGenerator.generateSpanId();
      request.getHeaders().add(TraceConstants.PARENT_SPAN_ID, spanId);
      request.getHeaders().add(TraceConstants.SPAN_ID, childSpanId);
    }

    log.info("RestTemplate添加追踪信息");
    return execution.execute(request, body);
  }
}
