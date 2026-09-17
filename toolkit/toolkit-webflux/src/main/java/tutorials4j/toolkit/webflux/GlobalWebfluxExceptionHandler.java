package tutorials4j.toolkit.webflux;

import io.micrometer.tracing.Tracer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalWebfluxExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNoResourceFoundException(
      NoResourceFoundException e, ServerWebExchange exchange) {
    return handleException(e, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ProblemDetail handleWebExchangeBindException(
      WebExchangeBindException e, ServerWebExchange exchange) {
    ProblemDetail problemDetail =
        handleException(
            e,
            HttpStatus.UNPROCESSABLE_CONTENT,
            errorDetail -> {
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
              errorDetail.setFieldErrors(fieldErrors);
            });
    problemDetail.setTitle("绑定异常");
    return problemDetail;
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
