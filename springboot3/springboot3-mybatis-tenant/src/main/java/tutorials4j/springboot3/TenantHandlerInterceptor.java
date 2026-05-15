package tutorials4j.springboot3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器配置：为了确保每个请求都能正确设置和清除租户 ID
 *
 * @author Yun Jiao
 */
public class TenantHandlerInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 从请求头中获取租户ID并设置到上下文中
    String tenantId = request.getHeader("X-Tenant-ID");
    if (tenantId != null) {
      TenantContext.setCurrentTenantId(tenantId);
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    // 清除租户ID
    TenantContext.clear();
  }
}
