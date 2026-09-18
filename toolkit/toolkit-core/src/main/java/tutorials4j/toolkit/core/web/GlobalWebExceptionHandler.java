package tutorials4j.toolkit.core.web;

import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.TooManyRequestException;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Order
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalWebExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(ErrorCodeException.class)
  public ProblemDetail handleErrorCodeException(ErrorCodeException e) {
    return handleErrorCodeException(e, ErrorDetailCustomizer.EMPTY);
  }

  @ExceptionHandler(TooManyRequestException.class)
  public ProblemDetail handleTooManyRequestException(TooManyRequestException e) {
    return handleException(e, HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ProblemDetail handleUnauthorizedException(UnauthorizedException e) {
    return handleException(e, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
    ProblemDetail problemDetail =
        handleException(
            e,
            HttpStatus.BAD_REQUEST,
            errorDetail -> {
              var fieldErrors =
                  e.getConstraintViolations().stream()
                      .collect(
                          Collectors.toMap(
                              violation -> violation.getPropertyPath().toString(),
                              violation ->
                                  violation.getMessage() == null ? "无效值" : violation.getMessage(),
                              (msg1, msg2) -> msg1 + "; " + msg2));
              errorDetail.setFieldErrors(fieldErrors);
            });
    problemDetail.setTitle("校验异常");
    return problemDetail;
  }

  @ExceptionHandler(BindException.class)
  public ProblemDetail handleBindException(BindException e) {
    ProblemDetail problemDetail =
        handleException(
            e,
            HttpStatus.BAD_REQUEST,
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
    problemDetail.setTitle("校验异常");
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception e) {
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
