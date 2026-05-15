package tutorials4j.framework.web.mvc.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.web.mvc.support.CachedBodyHttpServletRequest;

/**
 * 缓存请求体内容的过滤器。
 *
 * <p>该过滤器用于将原始的 {@link HttpServletRequest} 包装为 {@link CachedBodyHttpServletRequest}，
 * 从而支持后续对请求体的多次读取。包装前会检查请求体的 Content-Length 是否超过配置的最大允许长度， 若超过则放弃包装并记录警告日志，此时原始请求体将不可重复读取。
 *
 * <p>过滤器会避免重复包装同一个请求（即如果请求已经是 {@code CachedHttpServletRequestWrapper} 实例， 则直接放行）。
 *
 * @author Yun Jiao
 * @see CachedBodyHttpServletRequest
 */
@Slf4j
@RequiredArgsConstructor
public class CachedBodyRequestFilter implements Filter {

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
