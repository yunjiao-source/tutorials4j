package tutorials4j.framework.web.security.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.cache.core.exception.CounterOverflowException;
import tutorials4j.framework.common.spring.util.SessionUtils;
import tutorials4j.framework.web.core.annotation.Idempotent;
import tutorials4j.framework.web.core.exception.WebErrorCode;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * 幂等性校验的处理器拦截器。
 *
 * <p>拦截带有 {@link Idempotent} 注解的处理器方法，在方法执行前进行幂等性检查。 通过 {@link IdempotentCacheTemplate}
 * 对当前请求生成的唯一键进行计数， 由于该模板的最大次数固定为 1，因此同一请求键的第二次访问将抛出异常。
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
  /** 用于在请求属性中存放幂等键的属性名。 */
  private static final String IDEMPOTENT_ATTRIBUTE = "Idempotent";

  private final IdempotentCacheTemplate idempotentCacheTemplate;

  /**
   * 请求前置处理：对标注 {@link Idempotent} 的处理器方法进行幂等计数， 同一请求键的重复访问将抛出幂等校验失败异常。
   *
   * @param request HTTP 请求对象
   * @param response HTTP 响应对象
   * @param handler 处理器对象
   * @return 始终返回 true，允许请求继续执行
   * @throws Exception 幂等计数超限时抛出幂等校验失败异常
   */
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
        throw WebErrorCode.WEB_IDEMPOTENT_FAILURE.throwed().param("key", key);
      }
    }

    return true;
  }
}
