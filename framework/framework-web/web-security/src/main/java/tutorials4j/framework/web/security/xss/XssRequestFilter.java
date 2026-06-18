package tutorials4j.framework.web.security.xss;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * XSS 攻击防护过滤器。
 *
 * <p>该过滤器将原始的 {@link HttpServletRequest} 包装为 {@link XssHttpServletRequestWrapper}， 从而对请求参数、请求头等数据进行
 * AntiSamy 清洗，防止跨站脚本攻击。
 *
 * @author Yun Jiao
 * @see XssHttpServletRequestWrapper
 */
@Slf4j
public class XssRequestFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);
    if (log.isDebugEnabled()) {
      log.debug("Xss攻击, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }
    filterChain.doFilter(xssRequest, response);
  }
}
