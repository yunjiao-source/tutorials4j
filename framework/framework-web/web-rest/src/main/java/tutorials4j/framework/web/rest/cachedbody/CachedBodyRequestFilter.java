package tutorials4j.framework.web.rest.cachedbody;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存请求体内容的过滤器。
 *
 * <p>该过滤器用于将原始的 {@link HttpServletRequest} 包装为 {@link CachedBodyHttpServletRequest}，
 * 从而支持后续对请求体的多次读取。
 *
 * <p>过滤器会避免重复包装同一个请求：如果请求已经是 {@link CachedBodyHttpServletRequest} 实例则直接放行。
 *
 * @author Yun Jiao
 * @see CachedBodyHttpServletRequest
 */
@Slf4j
@RequiredArgsConstructor
public class CachedBodyRequestFilter implements Filter {

  /**
   * 将 {@link HttpServletRequest} 包装为 {@link CachedBodyHttpServletRequest} 后继续传递给过滤器链。
   *
   * <p>仅当请求为 {@link HttpServletRequest} 且尚未被缓存包装时才会进行包装，否则直接放行。
   *
   * @param request 待过滤的请求
   * @param response 响应对象
   * @param chain 过滤器链
   * @throws IOException 处理过程中发生 I/O 错误
   * @throws ServletException 处理过程中发生 Servlet 异常
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest httpRequest) {
      // 避免重复包装
      if (!(httpRequest instanceof CachedBodyHttpServletRequest)) {
        httpRequest = new CachedBodyHttpServletRequest(httpRequest);
      }
      chain.doFilter(httpRequest, response);
    } else {
      chain.doFilter(request, response);
    }
  }
}
