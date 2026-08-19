package tutorials4j.framework.captcha.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.captcha.annotation.CaptchaAuth;
import tutorials4j.framework.captcha.exception.CaptchaErrorCode;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * 验证码认证拦截器。
 *
 * <p>对标注了 {@link CaptchaAuth} 注解的处理器方法，从请求头中读取验证码键、类别与验证码值，
 * 校验通过后在响应头标记认证成功；参数不完整或校验失败时抛出对应的验证码错误码异常。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaAuthHandlerInterceptor implements HandlerInterceptor {
  private final CaptchaServiceFactory captchaServiceFactory;

  /**
   * 请求处理前执行验证码认证。
   *
   * @param request 当前请求
   * @param response 当前响应
   * @param handler 目标处理器
   * @return 始终返回 {@code true}，认证通过后放行请求
   * @throws Exception 验证码参数不完整或校验失败时抛出验证码错误码异常
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("验证码认证, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    CaptchaAuth captchaAuth = WebUtils.getHandlerMethodAnnotation(handler, CaptchaAuth.class);
    if (ObjectUtils.isNotEmpty(captchaAuth)) {

      String key = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_KEY);
      String category = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_CATEGORY);
      String code = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_CAPTCHA_CODE);

      // 1. 参数校验
      if (StringUtils.isAnyBlank(key, category, code)) {
        throw CaptchaErrorCode.CAPTCHA_PARAMETERS_INCOMPLETE.throwed();
      }

      if (!captchaServiceFactory.findService(category).verify(key, code)) {
        throw CaptchaErrorCode.CAPTCHA_VERIFY_FAILURE.throwed();
      }

      response.setHeader(DefaultConsts.HTTP_HEADER_CAPTCHA_AUTH, "SUCCESS");
    }

    return true;
  }
}
