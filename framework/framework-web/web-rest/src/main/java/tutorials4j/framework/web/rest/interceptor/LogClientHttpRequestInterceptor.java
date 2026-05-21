package tutorials4j.framework.web.rest.interceptor;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.spring.util.HttpRequestUtils;

/**
 * 客户端HTTP请求日志拦截器。
 *
 * <p>该拦截器实现了 {@link org.springframework.http.client.ClientHttpRequestInterceptor} 接口，
 * 用于在客户端HTTP请求执行前记录请求信息（包括请求头、请求体等）， 并在收到响应后记录响应信息（包括响应头、响应体等）。
 *
 * <p>日志记录功能委托给 {@link HttpRequestUtils} 工具类， 通过 {@link HttpRequestUtils#requestLogger(HttpRequest,
 * byte[])} 和 {@link HttpRequestUtils#responseLogger(ClientHttpResponse)} 方法完成。
 *
 * @author Yun Jiao
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see HttpRequestUtils
 */
@Slf4j
public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    // 请求日志记录
    HttpRequestUtils.requestLogger(request, body);
    // 执行实际请求
    ClientHttpResponse response = execution.execute(request, body);
    // 响应日志记录
    HttpRequestUtils.responseLogger(response);
    return response;
  }
}
