package tutorials4j.framework.web.security.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.cache.core.exception.CounterOverflowException;
import tutorials4j.framework.common.spring.util.SessionUtils;
import tutorials4j.framework.web.core.annotation.AccessLimited;
import tutorials4j.framework.web.core.exception.WebErrorCode;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * 访问频率限制的处理器拦截器。
 *
 * <p>拦截带有 {@link AccessLimited} 注解的处理器方法，在方法执行前进行访问次数校验。 通过 {@link AccessLimitedCacheTemplate}
 * 对当前请求生成的唯一键进行计数， 若超出允许的最大次数则抛出异常。
 *
 * <p>请求键的生成由 {@link SessionUtils#generateRequestKey(HttpServletRequest)} 负责， 通常结合用户标识、请求 URI、IP
 * 等信息，确保不同请求的区分度。
 *
 * @author Yun Jiao
 * @see AccessLimited
 * @see AccessLimitedCacheTemplate
 * @see HandlerInterceptor
 */
@Slf4j
@RequiredArgsConstructor
public class AccessLimitedHandlerInterceptor implements HandlerInterceptor {
  private final AccessLimitedCacheTemplate accessLimitedCacheTemplate;

  /**
   * 请求前置处理：对标注 {@link AccessLimited} 的处理器方法进行访问次数计数， 超出允许的最大次数时抛出访问受限异常。
   *
   * @param request HTTP 请求对象
   * @param response HTTP 响应对象
   * @param handler 处理器对象
   * @return 始终返回 true，允许请求继续执行
   * @throws Exception 计数超限时抛出访问受限异常
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("访问限制, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    AccessLimited accessLimited = WebUtils.getHandlerMethodAnnotation(handler, AccessLimited.class);
    if (ObjectUtils.isNotEmpty(accessLimited)) {
      String key = SessionUtils.generateRequestKey(request);

      try {
        accessLimitedCacheTemplate.counting(key, accessLimited.maxTimes(), true);
      } catch (CounterOverflowException e) {
        throw WebErrorCode.WEB_ACCESS_LIMITED
            .throwed()
            .param("key", key)
            .param("maxTimes", accessLimited.maxTimes());
      }
    }

    return true;
  }
}
