package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;
import tutorials4j.framework.common.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(30)
public class GlobalExceptionHandler {
  private static final Map<String, ErrorCode> EXCEPTION_DICTIONARY = new HashMap<>();

  static {
    EXCEPTION_DICTIONARY.put(
        "HttpMediaTypeNotSupportedException", BaseErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE);
    EXCEPTION_DICTIONARY.put(
        "HttpMediaTypeNotAcceptableException", BaseErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE);
    EXCEPTION_DICTIONARY.put("IllegalArgumentException", BaseErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
    EXCEPTION_DICTIONARY.put("NullPointerException", BaseErrorCode.NULL_POINTER_EXCEPTION);
    EXCEPTION_DICTIONARY.put("IOException", BaseErrorCode.IO_EXCEPTION);
    EXCEPTION_DICTIONARY.put("TypeMismatchException", BaseErrorCode.TYPE_MISMATCH_EXCEPTION);
    EXCEPTION_DICTIONARY.put(
        "MissingServletRequestParameterException",
        BaseErrorCode.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION);
  }

  @ExceptionHandler(BaseRuntimeException.class)
  public Result<Void> handleBaseException(
      BaseRuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    Result<Void> result =
        ex.getResult()
            .path(request.getRequestURI())
            .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));
    if (ex.getErrorCode().getFeedback().isSystemError()) {
      log.error("系统异常", ex);
    } else {
      log.warn("业务异常: {}", result);
    }
    response.setStatus(result.getStatus());
    return result;
  }

  @ExceptionHandler(Exception.class)
  public Result<Void> handleOtherException(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {
    ErrorCode initErrorCode = BaseErrorCode.INTERNAL_SERVER_ERROR;
    String className = ex.getClass().getSimpleName();
    if (EXCEPTION_DICTIONARY.containsKey(className)) {
      initErrorCode = EXCEPTION_DICTIONARY.get(className);
    }

    Result<Void> result = resolveException(ex, request.getRequestURI(), initErrorCode);
    response.setStatus(result.getStatus());
    return result;
  }

  public static Result<Void> resolveException(Exception ex, String path, ErrorCode defaultError) {
    Result<Void> result = Result.failure(defaultError);
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

    if (defaultError.getFeedback().isSystemError()) {
      result.errorStackTrace(ex.getStackTrace());
      log.error("系统异常", ex);
    } else {
      log.warn("系统警告: {}", result);
    }
    return result;
  }
}
