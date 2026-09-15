package tutorials4j.toolkit.oauth.satoken;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class SaTokenHandleException implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(NotLoginException.class)
  public ProblemDetail handlerUnauthorized(NotLoginException e) {
    return handleException(
        e,
        HttpStatus.UNAUTHORIZED,
        (errorDetail -> {
          errorDetail.addParam("code", e.getCode());
        }));
  }

  @ExceptionHandler({
    NotRoleException.class,
    NotPermissionException.class,
    DisableServiceException.class
  })
  public ProblemDetail handlerForbidden(NotLoginException e) {
    return handleException(
        e,
        HttpStatus.FORBIDDEN,
        (errorDetail -> {
          errorDetail.addParam("code", e.getCode());
        }));
  }

  @ExceptionHandler(SaTokenException.class)
  public ProblemDetail handlerSaTokenException(SaTokenException e) {
    return handleException(
        e,
        HttpStatus.INTERNAL_SERVER_ERROR,
        (errorDetail -> {
          errorDetail.addParam("code", e.getCode());
        }));
  }

  @Override
  public Tracer getTrace() {
    return tracer;
  }

  @Override
  public Logger getLog() {
    return log;
  }
}
