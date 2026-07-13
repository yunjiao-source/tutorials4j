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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler extends BaseExceptionHandler {
  @ExceptionHandler(ErrorCodeException.class)
  public ResponseEntity<Result<Void>> handleBaseException(
      ErrorCodeException ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI());
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    TypeMismatchException.class
  })
  public ResponseEntity<Result<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.BAD_REQUEST);
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<Result<Void>> handleMethodNotAllowed(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
  public ResponseEntity<Result<Void>> handleNotAcceptable(
      Exception ex, HttpServletRequest request) {
    return resolveException(ex, request.getRequestURI(), BaseErrorCode.NOT_ACCEPTABLE);
  }

  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  public ResponseEntity<Result<Void>> handleUnsupportedMediaType(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<Result<Void>> handleUnprocessableEntity(
      Exception ex, HttpServletRequest request) {

    return resolveException(ex, request.getRequestURI(), BaseErrorCode.INTERNAL_SERVER_ERROR);
  }

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
