package tutorials4j.framework.web.security.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.exception.CounterOverflowException;
import tutorials4j.framework.common.spring.util.SessionUtils;
import tutorials4j.framework.web.core.annotation.AccessLimited;
import tutorials4j.framework.web.core.exception.AccessLimitedException;

/**
 * 访问频率限制的处理器拦截器。
 *
 * <p>拦截带有 {@link AccessLimited} 注解的处理器方法，在方法执行前进行访问次数校验。 通过 {@link AccessLimitedCacheTemplate}
 * 对当前请求生成的唯一键进行计数， 若超出允许的最大次数则抛出 {@link AccessLimitedException}。
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

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("[WEB-SECURITY] 访问限制拦截器：{}", request.getRequestURI());
    }

    Method method = null;
    if (handler instanceof HandlerMethod handlerMethod) {
      method = handlerMethod.getMethod();
    }

    if (method == null) {
      return true;
    }

    AccessLimited accessLimited = method.getAnnotation(AccessLimited.class);
    if (ObjectUtils.isNotEmpty(accessLimited)) {
      String key = SessionUtils.generateRequestKey(request);

      try {
        accessLimitedCacheTemplate.counting(key, accessLimited.maxTimes(), true);
      } catch (CounterOverflowException e) {
        throw new AccessLimitedException(e);
      }
    }

    return true;
  }
}
