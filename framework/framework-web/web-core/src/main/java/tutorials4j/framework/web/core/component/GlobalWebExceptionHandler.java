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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(1)
public class GlobalWebExceptionHandler extends BaseExceptionHandler {

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
