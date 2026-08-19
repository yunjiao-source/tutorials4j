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
 * TOTP 双因素认证拦截器，在请求进入处理器之前校验 TOTP 动态验证码。
 *
 * <p>当目标处理方法标注了 {@link TotpAuth} 注解时，从请求头中读取用户名与验证码， 调用 {@link GoogleAuthService}
 * 进行校验；参数不完整或校验失败时抛出对应业务异常。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TotpAuthHandlerInterceptor implements HandlerInterceptor {
  private final GoogleAuthService googleAuthService;

  /**
   * 请求处理前执行 TOTP 校验；未标注 {@link TotpAuth} 的处理方法直接放行。
   *
   * @param request 当前请求
   * @param response 当前响应
   * @param handler 目标处理器
   * @return 始终返回 {@code true}，放行请求
   * @throws Exception 参数不完整或校验失败时抛出对应业务异常
   */
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
