package tutorials4j.toolkit.core.web;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.ErrorDetail;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface HandleException {

  default ProblemDetail handleErrorCodeException(
      ErrorCodeException e, ErrorDetailCustomizer customizer) {
    HttpStatus defaultHttpStatus = HttpStatus.UNPROCESSABLE_CONTENT;
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(defaultHttpStatus, e.getMessage());

    ErrorDetail errorDetail = e.getErrorDetail().setTraceId(getTraceId());
    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, defaultHttpStatus);
    return pd;
  }

  default ProblemDetail handleException(Throwable t, HttpStatus status) {
    return handleException(t, status, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleException(
      Throwable t, HttpStatus status, ErrorDetailCustomizer customizer) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, t.getMessage());

    ErrorDetail errorDetail = createErrorDetail(t).setCode(status.name());

    customizer.customize(errorDetail);
    resoveException(t, pd, errorDetail, status);
    return pd;
  }

  default ErrorDetail createErrorDetail(Throwable e) {
    return new ErrorDetail()
        .setTimestamp(Instant.now())
        .setTraceId(getTraceId())
        .setClassName(e.getClass().getName());
  }

  Tracer getTrace();

  default String getTraceId() {
    Span span = getTrace().currentSpan();
    return span != null ? span.context().traceId() : null;
  }

  default void resoveException(
      Throwable t, ProblemDetail problemDetail, ErrorDetail errorDetail, HttpStatus httpStatus) {
    problemDetail.setProperty("errors", errorDetail);

    ErrorCategory category = ErrorCategory.of(httpStatus.series());
    if (category.serverSide) {
      errorDetail.setStackTrace(t.getStackTrace());
    }

    if (category.serverSide) {
      LogHolder.log.error("{} : {}", category.title, problemDetail, t);
    } else {
      LogHolder.log.warn("{} : {}", category.title, problemDetail);
    }

    problemDetail.setTitle(category.title);
  }

  @FunctionalInterface
  interface ErrorDetailCustomizer {
    ErrorDetailCustomizer EMPTY = (e) -> {};

    void customize(ErrorDetail errorDetail);
  }

  @Slf4j
  final class LogHolder {}
}
