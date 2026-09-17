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
    pd.setTitle(e.getMessage());

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
    pd.setTitle("系统异常");

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
    problemDetail.setProperty("errors", errorDetail);
    if (httpStatus.series() == Series.SERVER_ERROR) {
      errorDetail.setStackTrace(t.getStackTrace());
      getLog().error("服务器异常", t);
    } else if (httpStatus.series() == Series.CLIENT_ERROR) {
      getLog().warn("客户端异常", t);
    } else {
      getLog().warn("其他异常", t);
    }
  }

  @FunctionalInterface
  interface ErrorDetailCustomizer {
    ErrorDetailCustomizer EMPTY = (e) -> {};

    void customize(ErrorDetail errorDetail);
  }
}
