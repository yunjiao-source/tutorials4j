package tutorials4j.framework.web.core.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * Web MVC 全局异常处理器。
 *
 * <p>继承 {@link BaseExceptionHandler}，统一处理资源不存在、参数绑定校验等异常， 并转换为标准 {@link Result} 响应结构。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(1)
public class GlobalWebExceptionHandler extends BaseExceptionHandler {

  static {
    BaseExceptionHandler.registerThrowable(
        Pair.of(NoResourceFoundException.class, HttpStatus.NOT_FOUND));
  }
}
