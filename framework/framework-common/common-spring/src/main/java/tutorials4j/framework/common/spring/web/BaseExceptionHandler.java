package tutorials4j.framework.common.spring.web;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCodeException;

/**
 * 全局异常处理基类，负责将各类异常转换为统一的 {@link Result} 响应结构， 并根据错误码映射对应的 HTTP 状态码。
 *
 * @author Yun Jiao
 */
@Slf4j
public class BaseExceptionHandler {
  /** 错误码到 HTTP 状态码的映射表。 */
  private static final Map<ErrorCode, HttpStatus> errorCodeMap = new HashMap<>();

  private static final Map<Class<? extends Throwable>, HttpStatus> throwableMap = new HashMap<>();

  /**
   * 注册错误码与 HTTP 状态码的映射关系，已存在的映射不会被覆盖。
   *
   * @param pairs 待注册的错误码映射表
   */
  @SafeVarargs
  public static void registerErrorCode(Pair<ErrorCode, HttpStatus>... pairs) {
    Assert.notNull(pairs, "pairs must not be null");

    for (Pair<ErrorCode, HttpStatus> pair : pairs) {
      errorCodeMap.putIfAbsent(pair.getKey(), pair.getValue());
    }
  }

  protected static HttpStatus lookupErrorCode(ErrorCode errorCode) {
    return errorCodeMap.get(errorCode);
  }

  @SafeVarargs
  public static void registerThrowable(Pair<Class<? extends Throwable>, HttpStatus>... pairs) {
    Assert.notNull(pairs, "pairs must not be null");

    for (Pair<Class<? extends Throwable>, HttpStatus> pair : pairs) {
      throwableMap.putIfAbsent(pair.getKey(), pair.getValue());
    }
  }

  protected static HttpStatus lookupThrowable(Class<? extends Throwable> clazz) {
    return throwableMap.get(clazz);
  }

  /**
   * 解析业务错误码异常，根据错误码映射 HTTP 状态码并构建响应结果。
   *
   * @param ex 业务错误码异常
   * @param path 请求路径
   * @return 包含错误信息的响应实体
   */
  protected ResponseEntity<Result<Void>> resolveErrorCodeException(
      ErrorCodeException ex, String path) {
    HttpStatus status = lookupErrorCode(ex.getErrorCode());
    if (status == null) {
      status = HttpStatus.UNPROCESSABLE_ENTITY;
    }

    return resolveException(ex, path, ex.getResult(), status);
  }

  /**
   * 解析通用异常，根据错误码映射 HTTP 状态码并构建失败响应结果。
   *
   * @param ex 异常对象
   * @param path 请求路径
   * @return 包含错误信息的响应实体
   */
  protected ResponseEntity<Result<Void>> resolveException(Exception ex, String path) {
    HttpStatus status = lookupThrowable(ex.getClass());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    Result<Void> result = Result.failure(BaseErrorCode.SYSTEM_EXCPEITON.getFeedback());
    handleResult(ex, path, result, status);
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(result);
  }

  /**
   * 填充响应结果的路径、异常详情与追踪 ID，并补充字段校验错误信息，最后按状态码输出日志并返回响应。
   *
   * @param ex 异常对象
   * @param path 请求路径
   * @param result 待填充的响应结果
   * @param status HTTP 状态码
   * @return 包含错误信息的响应实体
   */
  protected ResponseEntity<Result<Void>> resolveException(
      Exception ex, String path, Result<Void> result, HttpStatus status) {
    handleResult(ex, path, result, status);
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(result);
  }

  protected void handleResult(Exception ex, String path, Result<Void> result, HttpStatus status) {
    result
        .path(path)
        .errorClassName(ex.getClass().getName())
        .errorDetail(ex.getMessage())
        .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));

    switch (ex) {
      case WebExchangeBindException webExchangeBindException -> {
        Map<String, String> fieldErrors =
            webExchangeBindException.getBindingResult().getFieldErrors().stream()
                .collect(
                    Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "无效值" : fe.getDefaultMessage(),
                        (a, b) -> a));
        result.fieldErrors(fieldErrors);
      }
      case ConstraintViolationException constraintViolationException -> {
        Map<String, String> fieldErrors =
            constraintViolationException.getConstraintViolations().stream()
                .collect(
                    Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation ->
                            violation.getMessage() == null ? "无效值" : violation.getMessage(),
                        (msg1, msg2) -> msg1));
        result.fieldErrors(fieldErrors);
      }
      case BindException bindException -> {
        Map<String, String> fieldErrors =
            bindException.getBindingResult().getFieldErrors().stream()
                .collect(
                    Collectors.toMap(
                        FieldError::getField,
                        fieldError ->
                            fieldError.getDefaultMessage() == null
                                ? "无效值"
                                : fieldError.getDefaultMessage(),
                        (msg1, msg2) -> msg1));
        result.fieldErrors(fieldErrors);
      }
      default -> {}
    }

    if (status.series() == Series.SERVER_ERROR) {
      result.errorStackTrace(ex.getStackTrace());
      log.error("服务器异常: {}", result, ex);
    } else if (status.series() == Series.CLIENT_ERROR) {
      log.warn("客户端异常：{}", result, ex);
    } else {
      log.warn("其他异常: {}", result, ex);
    }
  }
}
