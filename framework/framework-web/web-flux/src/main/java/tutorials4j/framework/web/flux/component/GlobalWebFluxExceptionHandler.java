package tutorials4j.framework.web.flux.component;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.web.GlobalExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(10)
public class GlobalWebFluxExceptionHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Result<Void>>> handleWebExchangeBindException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    fieldError ->
                        fieldError.getDefaultMessage() == null
                            ? "无效值"
                            : fieldError.getDefaultMessage(),
                    (msg1, msg2) -> msg1));
    return buildValidationErrorResult(ex, fieldErrors, exchange);
  }

  // 因请求体解析失败导致
  @ExceptionHandler(ServerWebInputException.class)
  public Mono<ResponseEntity<Result<Void>>> handleServerWebInputException(
      ServerWebInputException ex, ServerWebExchange exchange) {
    Result<Void> result =
        GlobalExceptionHandler.resolveException(
            ex, exchange.getRequest().getPath().value(), BaseErrorCode.SERVER_ERROR);
    return Mono.just(ResponseEntity.status(result.getStatus()).body(result));
  }

  private Mono<ResponseEntity<Result<Void>>> buildValidationErrorResult(
      Exception ex, Map<String, String> fieldErrors, ServerWebExchange exchange) {
    Result<Void> result =
        GlobalExceptionHandler.resolveException(
                ex, exchange.getRequest().getPath().value(), BaseErrorCode.VALIDATION_FAILED)
            .fieldErrors(fieldErrors);
    return Mono.just(ResponseEntity.status(result.getStatus()).body(result));
  }
}
