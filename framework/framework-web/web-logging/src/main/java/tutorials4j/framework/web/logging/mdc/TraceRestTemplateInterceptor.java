package tutorials4j.framework.web.logging.mdc;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * RestTemplate 客户端请求拦截器，用于在同步 HTTP 调用中向下游服务传递链路追踪信息。
 *
 * <p>该拦截器从当前线程的 MDC 中获取 traceId 和 spanId，然后将它们添加到 HTTP 请求头中。 同时为当前调用生成一个新的子 spanId，并将原始的 spanId 作为
 * parentSpanId 一同传递， 使得下游服务能够还原完整的调用层级关系。
 *
 * @author Yun Jiao
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see org.slf4j.MDC
 * @since 1.0
 */
@Slf4j
public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

  /**
   * 从当前 MDC 读取 traceId 和 spanId 并写入请求头，同时为本次调用生成新的子 spanId 并将原 spanId 作为 parentSpanId 传递给下游服务。
   *
   * @param request HTTP 请求对象
   * @param body 请求体内容
   * @param execution 请求执行器
   * @return 下游返回的客户端响应
   * @throws IOException 请求执行过程中发生 I/O 异常
   */
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    // 传递追踪信息到下游服务
    String traceId = MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID);
    String spanId = MDC.get(DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID);

    if (traceId != null) {
      request.getHeaders().add(DefaultConsts.HTTP_HEADER_TRACE_ID, traceId);
    }
    if (spanId != null) {
      // 生成新的子span
      String childSpanId = WebUtils.generateSpanId();
      request.getHeaders().add(DefaultConsts.HTTP_HEADER_TRACE_PARENT_SPAN_ID, spanId);
      request.getHeaders().add(DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID, childSpanId);
    }

    if (log.isDebugEnabled()) {
      log.debug("跟踪信息, method={}, uri={}", request.getMethod(), request.getURI());
    }

    return execution.execute(request, body);
  }
}
