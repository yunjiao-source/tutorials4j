package tutorials4j.framework.tenant.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.spring.util.HeaderUtils;

/**
 * 租户拦截器：从请求头中解析租户 ID 并写入租户上下文，请求结束后清除，防止内存泄漏。
 *
 * @author Yun Jiao
 */
@Slf4j
public class TenantHandlerInterceptor implements HandlerInterceptor {

  /**
   * 请求处理前从请求头中解析租户 ID，并写入租户上下文。
   *
   * @param request 当前 HTTP 请求
   * @param response 当前 HTTP 响应
   * @param handler 目标处理器
   * @return 始终返回 true，放行请求
   */
  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String tenant = HeaderUtils.getTenantId(request);
    if (StringUtils.hasText(tenant)) {
      TenantContextHolder.set(tenant);
    }

    if (log.isDebugEnabled()) {
      log.debug("租户拦截器, url={}, tenant={}", request.getRequestURI(), tenant);
    }
    return true;
  }

  /**
   * 请求结束后清除租户上下文，防止内存泄漏。
   *
   * @param request 当前 HTTP 请求
   * @param response 当前 HTTP 响应
   * @param handler 目标处理器
   * @param ex 处理过程中抛出的异常，无异常时为 null
   */
  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    TenantContextHolder.clear(); // 请求结束后清除，防止内存泄漏
  }
}
