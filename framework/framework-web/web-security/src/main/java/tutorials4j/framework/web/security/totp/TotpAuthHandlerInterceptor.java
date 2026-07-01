package tutorials4j.framework.web.security.totp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.common.spring.util.SecurityUtils;
import tutorials4j.framework.web.core.annotation.TotpAuth;
import tutorials4j.framework.web.core.exception.WebErrorCode;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TotpAuthHandlerInterceptor implements HandlerInterceptor {
  private final GoogleAuthService googleAuthService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("TOT认证, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    TotpAuth totpAuth = WebUtils.getHandlerMethodAnnotation(handler, TotpAuth.class);
    if (ObjectUtils.isNotEmpty(totpAuth)) {
      String userName = HeaderUtils.getHeader(request, totpAuth.userName());
      if (StringUtils.isBlank(userName)) {
        userName = SecurityUtils.getAccount();
      }
      String code = HeaderUtils.getHeader(request, totpAuth.authCode());

      // 1. 参数校验
      if (StringUtils.isAnyBlank(userName, code)) {
        throw WebErrorCode.WEB_TOTP_PARAMETERS_INCOMPLETE.throwed();
      }
      if (!googleAuthService.verifyByUserName(userName, Integer.parseInt(code))) {
        throw WebErrorCode.WEB_TOTP_VERIFY_FAILURE.throwed();
      }

      response.setHeader(DefaultConsts.HTTP_HEADER_CAPTCHA_AUTH, "SUCCESS");
    }

    return true;
  }
}
