package tutorials4j.framework.common.spring.web;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCodeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class BaseExceptionHandler {
  private static final Map<ErrorCode, HttpStatus> errorCodeMap = new HashMap<>();

  public static void registeErrorCode(Map<ErrorCode, HttpStatus> tmpErrorCodeMap) {
    Assert.notNull(tmpErrorCodeMap, "tmpErrorCodeMap must not be null");

    tmpErrorCodeMap.forEach(errorCodeMap::putIfAbsent);
  }

  protected ResponseEntity<Result<Void>> resolveException(ErrorCodeException ex, String path) {
    HttpStatus status = errorCodeMap.get(ex.getErrorCode());
    if (status == null) {
      status = HttpStatus.UNPROCESSABLE_ENTITY;
    }

    return resolveException(ex, path, ex.getResult(), status);
  }

  protected ResponseEntity<Result<Void>> resolveException(
      Exception ex, String path, ErrorCode errorCode) {
    HttpStatus status = errorCodeMap.get(errorCode);
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    Result<Void> result = Result.failure(errorCode.getFeedback());
    return resolveException(ex, path, result, status);
  }

  private ResponseEntity<Result<Void>> resolveException(
      Exception ex, String path, Result<Void> result, HttpStatus status) {
    result
        .path(path)
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
      log.warn("客户端异常：{}", result);
    } else {
      log.warn("其他异常: {}", result, ex);
    }
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(result);
  }
}
