package tutorials4j.framework.oauth.satoken.component;

import cn.dev33.satoken.sign.exception.SaSignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.framework.auth.core.common.OAuthErrorCode;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler extends BaseExceptionHandler {
  @ExceptionHandler(SaSignException.class)
  public ResponseEntity<Result<Void>> handleSaSignException(
      NoResourceFoundException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), OAuthErrorCode.SIGN_CHECK_FAIL);
  }
}
