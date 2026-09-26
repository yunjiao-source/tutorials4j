package tutorials4j.toolkit.core.web;

import io.micrometer.tracing.Tracer;
import java.time.Instant;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
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
    var defaultHttpStatus = HttpStatus.UNPROCESSABLE_CONTENT;
    var pd = ProblemDetail.forStatusAndDetail(defaultHttpStatus, e.getDetail());

    var errorDetail = e.getErrorDetail().setTraceId(getTraceId());
    customizer.customize(errorDetail);
    resoveException(e, pd, errorDetail, defaultHttpStatus);
    return pd;
  }

  default ProblemDetail handleException(Throwable t, HttpStatus status) {
    return handleException(t, status, ErrorDetailCustomizer.EMPTY);
  }

  default ProblemDetail handleException(
      Throwable t, HttpStatus status, ErrorDetailCustomizer customizer) {
    var pd = ProblemDetail.forStatusAndDetail(status, t.getMessage());

    var errorDetail = createErrorDetail(t).setCode(status.name());

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

  Logger getLog();

  default String getTraceId() {
    var span = getTrace().currentSpan();
    return span != null ? span.context().traceId() : null;
  }

  default void resoveException(
      Throwable t, ProblemDetail problemDetail, ErrorDetail errorDetail, HttpStatus httpStatus) {
    var title = "";
    problemDetail.setProperty("errors", errorDetail);
    if (httpStatus.series() == Series.SERVER_ERROR) {
      errorDetail.setStackTrace(t.getStackTrace());
      title = "系统端异常";
      getLog().error(title, t);
    } else if (httpStatus.series() == Series.CLIENT_ERROR) {
      title = "客户端异常";
      getLog().warn(title, t);
    } else {
      title = "其他异常";
      getLog().warn("title", t);
    }
    problemDetail.setTitle(title);
  }

  @FunctionalInterface
  interface ErrorDetailCustomizer {
    ErrorDetailCustomizer EMPTY = (e) -> {};

    void customize(ErrorDetail errorDetail);
  }
}
