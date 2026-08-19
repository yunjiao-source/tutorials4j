package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCode;
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
    return resolveException(ex, request.getRequestURI());
  }

  /**
   * 处理请求体不可读、缺少请求参数等客户端请求格式错误的异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    TypeMismatchException.class
  })
  public ResponseEntity<Result<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.BAD_REQUEST);
  }

  /**
   * 处理请求方法不支持的异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<Result<Void>> handleMethodNotAllowed(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.METHOD_NOT_ALLOWED);
  }

  /**
   * 处理客户端不可接受的响应媒体类型异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
  public ResponseEntity<Result<Void>> handleNotAcceptable(
      Exception ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.NOT_ACCEPTABLE);
  }

  /**
   * 处理请求媒体类型不支持的异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  public ResponseEntity<Result<Void>> handleUnsupportedMediaType(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * 处理非法参数异常。
   *
   * @param ex 异常对象
   * @param request HTTP 请求对象
   * @return 包含错误信息的响应实体
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<Result<Void>> handleUnprocessableEntity(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.INTERNAL_SERVER_ERROR);
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
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.INTERNAL_SERVER_ERROR);
  }

  static {
    Map<ErrorCode, HttpStatus> map =
        Map.of(
            BaseErrorCode.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR,
            BaseErrorCode.WRAP_CHECK_EXCEPTION,
            HttpStatus.INTERNAL_SERVER_ERROR,
            BaseErrorCode.UNPROCESSABLE_ENTITY,
            HttpStatus.UNPROCESSABLE_ENTITY,
            BaseErrorCode.UNSUPPORTED_MEDIA_TYPE,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            BaseErrorCode.NOT_ACCEPTABLE,
            HttpStatus.NOT_ACCEPTABLE,
            BaseErrorCode.METHOD_NOT_ALLOWED,
            HttpStatus.METHOD_NOT_ALLOWED,
            BaseErrorCode.BAD_REQUEST,
            HttpStatus.BAD_REQUEST,
            BaseErrorCode.NOT_FOUND,
            HttpStatus.NOT_FOUND);
    BaseExceptionHandler.registeErrorCode(map);
  }
}
