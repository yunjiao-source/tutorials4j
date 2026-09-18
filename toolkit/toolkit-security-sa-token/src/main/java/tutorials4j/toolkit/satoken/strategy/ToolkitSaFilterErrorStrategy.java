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
public class ToolkitSaFilterErrorStrategy implements SaFilterErrorStrategy, HandleException {
  private final Tracer tracer;

  @ExceptionHandler(SaTokenException.class)
  public ProblemDetail handlerSaTokenException(SaTokenException e) {
    return handleThrowable(e);
  }

  @Override
  public Object run(Throwable t) {
    // 获取响应对象，用于设置状态码和响应头
    var response = SaHolder.getResponse();
    response.setHeader("Content-Type", "application/json;charset=UTF-8");

    var problemDetail = handleThrowable(t);
    response.setStatus(problemDetail.getStatus());
    return SaManager.getSaJsonTemplate().objectToJson(problemDetail);
  }

  private ProblemDetail handleThrowable(Throwable t) {
    // 获取响应对象，用于设置状态码和响应头
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

      return handleException(
          saTokenException,
          httpStatus,
          (errorDetail -> {
            errorDetail.addParam("code", saTokenException.getCode());
          }));
    }

    return handleException(t, HttpStatus.INTERNAL_SERVER_ERROR);
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
