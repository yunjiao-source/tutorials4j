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
import org.apache.commons.lang3.StringUtils;
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
    var deltail = "";
    // 获取响应对象，用于设置状态码和响应头
    if (t instanceof SaTokenException saTokenException) {
      var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      if (t instanceof NotLoginException notLoginException) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        deltail =
            switch (notLoginException.getType()) {
              case NotLoginException.TOKEN_TIMEOUT, NotLoginException.TOKEN_FREEZE -> "登录已过期，请重新登录";
              case NotLoginException.BE_REPLACED -> "当前账号已在其他设备登录，您已被强制下线";
              case NotLoginException.KICK_OUT -> "账号已被管理员强制下线";
              default -> "登录状态异常，请重新登录";
            };
      } else if (t instanceof NotPermissionException
          || t instanceof NotRoleException
          || t instanceof DisableServiceException
          || t instanceof BlockUrlException) {
        httpStatus = HttpStatus.FORBIDDEN;
        deltail = "没有访问权限，请联系管理员授权";
      }

      var problemDetail =
          handleException(
              saTokenException,
              httpStatus,
              (errorDetail -> {
                errorDetail.addParam("code", saTokenException.getCode());
              }));
      if (StringUtils.isNotBlank(deltail)) {
        problemDetail.setDetail(deltail);
      }
      return problemDetail;
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
