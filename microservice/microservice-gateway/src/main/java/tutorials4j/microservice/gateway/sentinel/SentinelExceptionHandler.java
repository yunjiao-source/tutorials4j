package tutorials4j.microservice.gateway.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
public class SentinelExceptionHandler implements HandleException {
  private final Tracer tracer;

  @ExceptionHandler(BlockException.class)
  public ProblemDetail handleBlockException(BlockException e) {
    var problemDetail = handleException(e, HttpStatus.TOO_MANY_REQUESTS);
    problemDetail.setDetail("操作太频繁，请稍后再试");
    return problemDetail;
  }

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleNotFoundException(NotFoundException e) {
    var problemDetail = handleException(e, HttpStatus.SERVICE_UNAVAILABLE);
    problemDetail.setDetail("无效服务");
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
