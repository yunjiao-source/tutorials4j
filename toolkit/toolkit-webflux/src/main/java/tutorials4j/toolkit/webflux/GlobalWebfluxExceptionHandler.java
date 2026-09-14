package tutorials4j.toolkit.webflux;

import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.TooManyRequestException;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Order
@RestControllerAdvice
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GlobalWebfluxExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(ErrorCodeException.class)
  public ProblemDetail handleErrorCodeException(ErrorCodeException e, ServerWebExchange exchange) {
    return handleErrorCodeException(e);
  }

  @ExceptionHandler(TooManyRequestException.class)
  public ProblemDetail handleTooManyRequestException(
      TooManyRequestException e, ServerWebExchange exchange) {
    return handleException(e, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNoResourceFoundException(
      NoResourceFoundException e, ServerWebExchange exchange) {
    return handleException(e, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ProblemDetail handleWebExchangeBindException(
      WebExchangeBindException e, ServerWebExchange exchange) {
    return handleException(
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
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(
      ConstraintViolationException e, ServerWebExchange exchange) {
    return handleConstraintViolationException(e);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e, ServerWebExchange exchange) {
    return handleException(
        e,
        HttpStatus.INTERNAL_SERVER_ERROR,
        errorDetail -> {
          errorDetail.setTraceId(getTraceId());
        });
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
