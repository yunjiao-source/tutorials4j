package tutorials4j.framework.web.flux.component;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.web.AbstractExceptionHandler;
import tutorials4j.framework.common.spring.web.ExceptionMapping;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(100)
public class GlobalWebFluxExceptionHandler extends AbstractExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public Mono<ResponseEntity<Result<Void>>> handleNoResourceFoundException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    return buildResult(ex, exchange);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Result<Void>>> handleWebExchangeBindException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    return buildResult(ex, exchange);
  }

  // 因请求体解析失败导致
  @ExceptionHandler(ServerWebInputException.class)
  public Mono<ResponseEntity<Result<Void>>> handleServerWebInputException(
      ServerWebInputException ex, ServerWebExchange exchange) {
    return buildResult(ex, exchange);
  }

  private Mono<ResponseEntity<Result<Void>>> buildResult(Exception ex, ServerWebExchange exchange) {
    Result<Void> result = resolveException(ex, exchange.getRequest().getPath().value());
    return Mono.just(ResponseEntity.status(result.getStatus()).body(result));
  }

  @Override
  protected List<ExceptionMapping> getExceptionMappings() {
    List<ExceptionMapping> mappings = new ArrayList<>();
    mappings.add(
        new ExceptionMapping(WebExchangeBindException.class, BaseErrorCode.VALIDATION_FAILED));
    mappings.add(
        new ExceptionMapping(ServerWebInputException.class, BaseErrorCode.INTERNAL_SERVER_ERROR));
    mappings.add(
        new ExceptionMapping(NoResourceFoundException.class, BaseErrorCode.RESOURCE_NOT_FOUND));
    return mappings;
  }
}
