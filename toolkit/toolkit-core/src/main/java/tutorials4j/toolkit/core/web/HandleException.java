package tutorials4j.toolkit.core.web;

import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.ErrorDetail;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface HandleException {

  default ProblemDetail handleErrorCodeException(ErrorCodeException e) {
    return handleErrorCodeException(e, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleErrorCodeException(
      ErrorCodeException e, ErrorDetailCustomizer customizer) {
    var defaultHttpStatus = HttpStatus.UNPROCESSABLE_CONTENT;
    var pd = ProblemDetail.forStatusAndDetail(defaultHttpStatus, e.getDetail());
    pd.setTitle(e.getMessage());

    var errorDetail = e.getErrorDetail().setTraceId(getTraceId());
    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, defaultHttpStatus);
    return pd;
  }

  default ProblemDetail handleException(Exception e, HttpStatus status) {
    return handleException(e, status, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleException(
      Exception e, HttpStatus status, ErrorDetailCustomizer customizer) {
    var pd = ProblemDetail.forStatusAndDetail(status, e.getMessage());
    pd.setTitle("系统异常");

    var errorDetail = createErrorDetail(e).setCode(status.name());

    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, status);
    return pd;
  }

  default ProblemDetail handleBindException(BindException e) {
    return handleBindException(e, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleBindException(BindException e, ErrorDetailCustomizer customizer) {
    var defaultHttpStatus = HttpStatus.BAD_REQUEST;
    var pd = ProblemDetail.forStatusAndDetail(defaultHttpStatus, e.getMessage());
    pd.setTitle("校验异常");

    var fieldErrors =
        e.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    fieldError ->
                        fieldError.getDefaultMessage() == null
                            ? "无效值"
                            : fieldError.getDefaultMessage(),
                    (msg1, msg2) -> msg1));

    var errorDetail =
        createErrorDetail(e).setCode(defaultHttpStatus.name()).setFieldErrors(fieldErrors);

    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, defaultHttpStatus);
    return pd;
  }

  default ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
    return handleConstraintViolationException(e, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleConstraintViolationException(
      ConstraintViolationException e, ErrorDetailCustomizer customizer) {
    var defaultHttpStatus = HttpStatus.BAD_REQUEST;
    var pd = ProblemDetail.forStatusAndDetail(defaultHttpStatus, e.getMessage());
    pd.setTitle("校验异常");

    var fieldErrors =
        e.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage() == null ? "无效值" : violation.getMessage(),
                    (msg1, msg2) -> msg1 + "; " + msg2));

    var errorDetail =
        createErrorDetail(e).setCode(defaultHttpStatus.name()).setFieldErrors(fieldErrors);

    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, defaultHttpStatus);
    return pd;
  }

  default ErrorDetail createErrorDetail(Exception e) {
    return new ErrorDetail()
        .setTimestamp(Instant.now())
        .setTraceId(getTraceId())
        .setClassName(e.getClass().getName());
  }

  Tracer getTrace();

  Logger getLog();

  default String getTraceId() {
    var span = getTrace().currentSpan();
    return span != null ? span.context().traceId() : null;
  }

  default void resoveException(
      Exception e, ProblemDetail problemDetail, ErrorDetail errorDetail, HttpStatus httpStatus) {
    problemDetail.setProperty("errors", errorDetail);
    if (httpStatus.series() == Series.SERVER_ERROR) {
      errorDetail.setStackTrace(e.getStackTrace());
      getLog().error("服务器异常", e);
    } else if (httpStatus.series() == Series.CLIENT_ERROR) {
      getLog().warn("客户端异常", e);
    } else {
      getLog().warn("其他异常", e);
    }
  }

  @FunctionalInterface
  interface ErrorDetailCustomizer {
    ErrorDetailCustomizer EMPTY = (e) -> {};

    void customize(ErrorDetail errorDetail);
  }
}
