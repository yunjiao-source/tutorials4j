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
 * WebFlux 全局异常处理器。
 *
 * <p>继承 {@link BaseExceptionHandler}，处理资源不存在（{@link NoResourceFoundException}） 与参数绑定校验失败（{@link
 * WebExchangeBindException}）两类异常， 并统一转换为标准 {@link Result} 响应结构。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(2)
public class GlobalWebFluxExceptionHandler extends BaseExceptionHandler {

  /**
   * 处理资源不存在异常，返回 404 响应。
   *
   * @param ex 参数绑定异常（此处仅用于复用异常解析逻辑）
   * @param exchange 当前 WebFlux 交换对象，用于获取请求路径
   * @return 包含 NOT_FOUND 错误码的响应结果
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public Mono<ResponseEntity<Result<Void>>> handleNoResourceFoundException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    ResponseEntity<Result<Void>> entity =
        resolveException(ex, exchange.getRequest().getPath().value(), BaseErrorCode.NOT_FOUND);
    return Mono.just(entity);
  }

  /**
   * 处理参数绑定校验异常，返回 400 响应。
   *
   * @param ex 参数绑定异常
   * @param exchange 当前 WebFlux 交换对象，用于获取请求路径
   * @return 包含 BAD_REQUEST 错误码的响应结果
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Result<Void>>> handleWebExchangeBindException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    ResponseEntity<Result<Void>> entity =
        resolveException(ex, exchange.getRequest().getPath().value(), BaseErrorCode.BAD_REQUEST);
    return Mono.just(entity);
  }
}
