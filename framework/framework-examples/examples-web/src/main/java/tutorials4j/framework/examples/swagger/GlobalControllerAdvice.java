package tutorials4j.framework.examples.swagger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理器，统一捕获各类异常并将其转换为标准错误响应（{@link Problem} 或 {@link ErrorMessage}）。
 *
 * @author Yun Jiao
 */
@Slf4j
@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalControllerAdvice // extends ResponseEntityExceptionHandler
 {
  /**
   * 处理未捕获的异常，返回包含日志引用号的通用错误响应。
   *
   * @param e 捕获的异常
   * @return 包含日志引用号的错误响应
   */
  @ExceptionHandler(Throwable.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Problem> problem(final Throwable e) {
    String message = e.getMessage();
    // might actually prefer to use a geeric mesasge

    message = "Problem occured";
    UUID uuid = UUID.randomUUID();
    String logRef = uuid.toString();
    log.error("logRef=" + logRef, message, e);
    return new ResponseEntity<Problem>(
        new Problem(logRef, message), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 处理方法参数校验失败异常，返回字段错误与全局错误信息。
   *
   * @param ex 方法参数校验异常
   * @return 包含错误信息的响应
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
    List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
    String error;
    for (FieldError fieldError : fieldErrors) {
      error = fieldError.getField() + ", " + fieldError.getDefaultMessage();
      errors.add(error);
    }
    for (ObjectError objectError : globalErrors) {
      error = objectError.getObjectName() + ", " + objectError.getDefaultMessage();
      errors.add(error);
    }
    ErrorMessage errorMessage = new ErrorMessage(errors);

    // Object result=ex.getBindingResult();//instead of above can allso pass the more detailed
    // bindingResult
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理约束违规异常，返回违规信息列表。
   *
   * @param ex 约束违规异常
   * @return 包含错误信息的响应
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> handleConstraintViolatedException(
      ConstraintViolationException ex) {
    Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

    List<String> errors = new ArrayList<>(constraintViolations.size());
    String error;
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {

      error = constraintViolation.getMessage();
      errors.add(error);
    }

    ErrorMessage errorMessage = new ErrorMessage(errors);
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理缺少请求参数异常，返回缺失参数名称及错误信息。
   *
   * @param ex 缺少请求参数异常
   * @return 包含错误信息的响应
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {

    List<String> errors = new ArrayList<>();
    String error = ex.getParameterName() + ", " + ex.getMessage();
    errors.add(error);
    ErrorMessage errorMessage = new ErrorMessage(errors);
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理不支持的媒体类型异常，返回不支持与支持的内容类型信息。
   *
   * @param ex 不支持的媒体类型异常
   * @return 包含错误信息的响应
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @ResponseStatus(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public ResponseEntity<ErrorMessage> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex) {
    String unsupported = "Unsupported content type: " + ex.getContentType();
    String supported =
        "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
    ErrorMessage errorMessage = new ErrorMessage(unsupported, supported);
    return new ResponseEntity<>(errorMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  /**
   * 处理请求体不可读异常，返回最具体的原因信息。
   *
   * @param ex 请求体不可读异常
   * @return 包含错误信息的响应
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    ErrorMessage errorMessage;
    if (mostSpecificCause != null) {
      String exceptionName = mostSpecificCause.getClass().getName();
      String message = mostSpecificCause.getMessage();
      errorMessage = new ErrorMessage(exceptionName, message);
    } else {
      errorMessage = new ErrorMessage(ex.getMessage());
    }
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }
}
