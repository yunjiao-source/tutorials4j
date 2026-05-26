package tutorials4j.springboot3.web.referer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 图片安全过滤器
 *
 * @author Yun Jiao
 */
public class ImageSecurityFilter extends OncePerRequestFilter {

  private List<String> allowedOrigins;
  private List<String> allowedIps;

  public ImageSecurityFilter(List<String> allowedOrigins, List<String> allowedIps) {
    this.allowedOrigins = allowedOrigins;
    this.allowedIps = allowedIps;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, ServletException, IOException {
    String referer = request.getHeader("Referer");
    String clientIp = request.getRemoteAddr(); // 获取客户端 IP

    // 检查 Referer
    if (referer != null) {
      boolean allowed = false;
      for (String origin : allowedOrigins) {
        if (referer.startsWith(origin)) {
          allowed = true;
          break;
        }
      }
      if (!allowed) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    // 检查 IP
    if (!allowedIps.contains(clientIp)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    filterChain.doFilter(request, response);
  }
}
