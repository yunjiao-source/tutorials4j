package tutorials4j.toolkit.webmvc;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalWebmvcExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNoResourceFoundException(NoResourceFoundException e) {
    return handleException(e, HttpStatus.NOT_FOUND);
  }

  @Override
  public Tracer getTrace() {
    return tracer;
  }
}
