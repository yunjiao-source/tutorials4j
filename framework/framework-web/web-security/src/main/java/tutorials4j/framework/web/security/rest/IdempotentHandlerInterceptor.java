package tutorials4j.framework.web.security.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.exception.CounterOverflowException;
import tutorials4j.framework.common.spring.util.SessionUtils;
import tutorials4j.framework.web.core.annotation.Idempotent;
import tutorials4j.framework.web.core.exception.IdempotentException;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * 幂等性校验的处理器拦截器。
 *
 * <p>拦截带有 {@link Idempotent} 注解的处理器方法，在方法执行前进行幂等性检查。 通过 {@link IdempotentCacheTemplate}
 * 对当前请求生成的唯一键进行计数， 由于该模板的最大次数固定为 1，因此同一请求键的第二次访问将抛出 {@link IdempotentException}。
 *
 * <p>请求键的生成由 {@link SessionUtils#generateRequestKey(HttpServletRequest)} 负责， 通常结合用户标识、请求
 * URI、请求参数等，确保同一请求的唯一性。
 *
 * @author Yun Jiao
 * @see Idempotent
 * @see IdempotentCacheTemplate
 * @see HandlerInterceptor
 */
@Slf4j
@RequiredArgsConstructor
public class IdempotentHandlerInterceptor implements HandlerInterceptor {
  private static final String IDEMPOTENT_ATTRIBUTE = "Idempotent";
  private final IdempotentCacheTemplate idempotentCacheTemplate;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("幂等, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    Idempotent idempotent = WebUtils.getHandlerMethodAnnotation(handler, Idempotent.class);
    if (ObjectUtils.isNotEmpty(idempotent)) {
      String key = SessionUtils.generateRequestKey(request);

      try {
        idempotentCacheTemplate.counting(key, true);
        request.setAttribute(IDEMPOTENT_ATTRIBUTE, key);
      } catch (CounterOverflowException e) {
        throw new IdempotentException(e);
      }
    }

    return true;
  }
}
