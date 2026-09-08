package tutorials4j.framework.web.flux.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import tutorials4j.framework.common.core.bean.Result;
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

  static {
    BaseExceptionHandler.registerThrowable(
        Pair.of(NoResourceFoundException.class, HttpStatus.NOT_FOUND));
  }
}
