package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCodeException;

/**
 * 全局异常处理器，通过 {@code @RestControllerAdvice} 统一捕获并转换各类异常为标准的响应结果。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler extends BaseExceptionHandler {
  /**
   * 处理业务错误码异常。
   *
   * @param ex 业务错误码异常
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler(ErrorCodeException.class)
  public ResponseEntity<Result<Void>> handleBaseException(
      ErrorCodeException ex, HttpServletRequest request) {
    return resolveErrorCodeException(ex, request.getRequestURI());
  }

  /**
   * 处理未被其他处理器捕获的其余异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<Void>> handleOtherException(
      Exception ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI());
  }

  static {
    BaseExceptionHandler.registerErrorCode(
        Pair.of(BaseErrorCode.SYSTEM_EXCPEITON, HttpStatus.INTERNAL_SERVER_ERROR),
        Pair.of(BaseErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS),
        Pair.of(BaseErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED),
        Pair.of(BaseErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN));
    BaseExceptionHandler.registerThrowable(
        Pair.of(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST),
        Pair.of(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST),
        Pair.of(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST),
        Pair.of(TypeMismatchException.class, HttpStatus.BAD_REQUEST),
        Pair.of(WebExchangeBindException.class, HttpStatus.BAD_REQUEST),
        Pair.of(HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED),
        Pair.of(HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE),
        Pair.of(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE),
        Pair.of(MethodArgumentNotValidException.class, HttpStatus.UNPROCESSABLE_ENTITY),
        Pair.of(BindException.class, HttpStatus.UNPROCESSABLE_ENTITY),
        Pair.of(ConstraintViolationException.class, HttpStatus.UNPROCESSABLE_ENTITY));
  }
}
