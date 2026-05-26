package tutorials4j.springboot3.data.orm.routingdatasourcejpa;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器
 *
 * @author Yun Jiao
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 从请求头中获取租户标识（也可以从Token、Session中解析）
    String tenantId = request.getHeader("X-Tenant-ID");
    if (StringUtils.hasText(tenantId)) {
      DataSourceContextHolder.setTenantId(tenantId);
    } else {
      // 可以设置默认租户或抛出异常
      DataSourceContextHolder.setTenantId("tenant_a");
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    DataSourceContextHolder.clear(); // 请求结束后清除，防止内存泄漏
  }
}
