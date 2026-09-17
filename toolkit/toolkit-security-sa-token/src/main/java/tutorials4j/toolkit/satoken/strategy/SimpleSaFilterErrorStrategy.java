package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
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
import tutorials4j.toolkit.satoken.exception.BlockUrlException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class SimpleSaFilterErrorStrategy implements SaFilterErrorStrategy, HandleException {
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
    DisableServiceException.class,
    BlockUrlException.class
  })
  public ProblemDetail handlerForbidden(SaTokenException e) {
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
  public Object run(Throwable t) {
    // 获取响应对象，用于设置状态码和响应头
    var response = SaHolder.getResponse();
    response.setHeader("Content-Type", "application/json;charset=UTF-8");

    if (t instanceof SaTokenException saTokenException) {
      var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      if (t instanceof NotLoginException) {
        httpStatus = HttpStatus.UNAUTHORIZED;
      } else if (t instanceof NotPermissionException
          || t instanceof NotRoleException
          || t instanceof DisableServiceException
          || t instanceof BlockUrlException) {
        httpStatus = HttpStatus.FORBIDDEN;
      }

      var problemDetail =
          handleException(
              saTokenException,
              httpStatus,
              (errorDetail -> {
                errorDetail.addParam("code", saTokenException.getCode());
              }));

      response.setStatus(problemDetail.getStatus());
      return SaManager.getSaJsonTemplate().objectToJson(problemDetail);
    }

    var problemDetail = handleException(t, HttpStatus.INTERNAL_SERVER_ERROR);
    response.setStatus(problemDetail.getStatus());
    return SaManager.getSaJsonTemplate().objectToJson(problemDetail);
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
