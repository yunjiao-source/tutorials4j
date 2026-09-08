package tutorials4j.framework.oauth.satoken.component;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(3)
public class SaTokenExceptionHandler extends BaseExceptionHandler {
  @ExceptionHandler(SaTokenException.class)
  public ResponseEntity<Result<Void>> handlerSaTokenException(
      SaTokenException e, HttpServletRequest request) {
    BaseErrorCode errorCode = BaseErrorCode.SYSTEM_EXCPEITON;

    if (e instanceof NotLoginException) { // 如果是未登录异常
      errorCode = BaseErrorCode.UNAUTHORIZED;
    } else if (e instanceof NotRoleException
        || e instanceof NotPermissionException
        || e instanceof DisableServiceException) {
      errorCode = BaseErrorCode.FORBIDDEN;
    }

    Result<Void> result = Result.failure(errorCode.getFeedback());
    result.errorParams(List.of(Pair.of("code", e.getCode())));

    HttpStatus status = lookupErrorCode(errorCode);
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return resolveException(e, request.getRequestURI(), result, status);
  }
}
