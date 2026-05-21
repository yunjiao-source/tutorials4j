package tutorials4j.framework.tenant.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.spring.util.HeaderUtils;

/**
 * 租户拦截器
 *
 * @author Yun Jiao
 */
@Slf4j
public class TenantHandlerInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String tenant = HeaderUtils.getTenantId(request);
    if (StringUtils.hasText(tenant)) {
      TenantContextHolder.set(tenant);
    }

    if (log.isDebugEnabled()) {
      log.debug("[DATA-CORE] 租户请求拦截器：url={}, tenant={}", request.getRequestURI(), tenant);
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    TenantContextHolder.clear(); // 请求结束后清除，防止内存泄漏
  }
}
