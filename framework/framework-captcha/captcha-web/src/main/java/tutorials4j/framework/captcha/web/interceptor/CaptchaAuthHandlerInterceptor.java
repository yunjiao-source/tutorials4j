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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaAuthHandlerInterceptor implements HandlerInterceptor {
  private final CaptchaServiceFactory captchaServiceFactory;

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
