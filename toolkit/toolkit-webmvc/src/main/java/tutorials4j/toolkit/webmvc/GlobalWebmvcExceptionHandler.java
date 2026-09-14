package tutorials4j.toolkit.webmvc;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
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
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalWebmvcExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(ErrorCodeException.class)
  public ProblemDetail handleErrorCodeException(ErrorCodeException e, HttpServletRequest request) {
    return handleErrorCodeException(e);
  }

  @ExceptionHandler(TooManyRequestException.class)
  public ProblemDetail handleTooManyRequestException(
      TooManyRequestException e, HttpServletRequest request) {
    return handleException(e, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNoResourceFoundException(
      NoResourceFoundException e, HttpServletRequest request) {
    return handleException(e, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(
      ConstraintViolationException e, HttpServletRequest request) {
    return handleConstraintViolationException(e);
  }

  @ExceptionHandler(BindException.class)
  public ProblemDetail handleBindException(BindException e, HttpServletRequest request) {
    return handleBindException(e);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e, HttpServletRequest request) {
    return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
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
