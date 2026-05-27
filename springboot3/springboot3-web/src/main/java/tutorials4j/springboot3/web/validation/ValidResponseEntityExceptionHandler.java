package tutorials4j.springboot3.web.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 校验消息扩展
 *
 * @author yangyunjiao
 */
@RestControllerAdvice
public class ValidResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ProblemDetail problemDetail = createProblemDetail(ex, status, "方法校验异常", null, null, request);

    List<String> errors = new ArrayList<>();
    for (ParameterValidationResult parameterResult : ex.getParameterValidationResults()) {
      // 每个参数可能有多个错误
      String message =
          parameterResult.getResolvableErrors().stream()
              .map(MessageSourceResolvable::getDefaultMessage)
              .collect(Collectors.joining(","));
      errors.add(message);
    }

    problemDetail.setProperty("errors", errors);

    return handleExceptionInternal(ex, problemDetail, headers, status, request);
  }

  /**
   * 重写 handleMethodArgumentNotValid 以自定义 @RequestBody 校验失败的返回格式 父类默认也会返回 ProblemDetail，但我们需要把具体的
   * field errors 放进去
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ProblemDetail problemDetail = createProblemDetail(ex, status, "方法参数校验异常", null, null, request);

    // 收集字段错误
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    // 关键：将错误详情放入 properties
    problemDetail.setProperty("errors", fieldErrors);

    return handleExceptionInternal(ex, problemDetail, headers, status, request);
  }

  /**
   * 处理 ConstraintViolationException 注意：ResponseEntityExceptionHandler 父类中没有直接对应
   * ConstraintViolationException 的方法 所以这里依然需要使用 @ExceptionHandler，但这不影响我们继承父类来处理其他异常
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    ProblemDetail problemDetail = createProblemDetail(ex, status, "违反约束异常", null, null, request);

    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    Map<String, String> fieldErrors =
        violations.stream()
            .collect(
                Collectors.toMap(
                    v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (e1, e2) -> e1));

    problemDetail.setProperty("errors", fieldErrors);

    return handleExceptionInternal(ex, problemDetail, null, status, request);
  }
}
