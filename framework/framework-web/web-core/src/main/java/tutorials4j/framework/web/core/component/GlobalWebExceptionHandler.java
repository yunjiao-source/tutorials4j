package tutorials4j.framework.web.core.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
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

  /**
   * 处理资源不存在异常，返回 404 响应。
   *
   * @param ex 资源不存在异常
   * @param request 当前请求，用于获取请求 URI
   * @return 包含 NOT_FOUND 错误码的响应结果
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Result<Void>> handleNoResourceFoundException(
      NoResourceFoundException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.NOT_FOUND);
  }

  /** 处理 @RequestBody 参数校验失败 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.UNPROCESSABLE_ENTITY);
  }

  /** 处理 @ModelAttribute 或表单参数校验失败 */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Result<Void>> handleWebBindException(
      BindException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.UNPROCESSABLE_ENTITY);
  }

  /** 处理方法参数校验（如 @RequestParam @Valid） */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<Void>> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.UNPROCESSABLE_ENTITY);
  }
}
