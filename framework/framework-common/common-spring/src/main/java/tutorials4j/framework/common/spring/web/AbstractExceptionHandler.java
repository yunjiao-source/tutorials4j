package tutorials4j.framework.common.spring.web;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractExceptionHandler {
  // 使用 List 保证顺序（子类在前，父类在后）
  private final List<ExceptionMapping> exceptionMappings = new ArrayList<>();

  protected abstract List<ExceptionMapping> getExceptionMappings();

  protected synchronized void initMappings() {
    if (!exceptionMappings.isEmpty()) {
      return;
    }

    exceptionMappings.addAll(getExceptionMappings());
  }

  protected Result<Void> resolveException(Exception ex, String path) {
    ErrorCode errorCode = lookupErrorCode(ex);

    Result<Void> result = Result.failure(errorCode);
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

    result.errorStackTrace(ex.getStackTrace());
    return result;
  }

  protected ErrorCode lookupErrorCode(Exception ex) {
    if (exceptionMappings.isEmpty()) {
      initMappings();
    }
    for (ExceptionMapping mapping : exceptionMappings) {
      // isAssignableFrom 可以匹配子类、实现类
      if (mapping.exceptionClass().isAssignableFrom(ex.getClass())) {
        return mapping.errorCode();
      }
    }
    // 若 List 未命中（比如没有加 Exception 兜底），返回默认 500
    return BaseErrorCode.INTERNAL_SERVER_ERROR;
  }
}
