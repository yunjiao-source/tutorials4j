package tutorials4j.framework.web.flux.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(2)
public class GlobalWebFluxExceptionHandler extends BaseExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public Mono<ResponseEntity<Result<Void>>> handleNoResourceFoundException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    ResponseEntity<Result<Void>> entity =
        resolveException(ex, exchange.getRequest().getPath().value(), BaseErrorCode.NOT_FOUND);
    return Mono.just(entity);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Result<Void>>> handleWebExchangeBindException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    ResponseEntity<Result<Void>> entity =
        resolveException(ex, exchange.getRequest().getPath().value(), BaseErrorCode.BAD_REQUEST);
    return Mono.just(entity);
  }
}
