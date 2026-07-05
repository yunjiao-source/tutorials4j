package tutorials4j.framework.web.core.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.web.AbstractExceptionHandler;
import tutorials4j.framework.common.spring.web.ExceptionMapping;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(200)
public class GlobalWebExceptionHandler extends AbstractExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public Result<Void> handleNoResourceFoundException(
      NoResourceFoundException ex, HttpServletRequest request, HttpServletResponse response) {
    return buildResult(ex, request, response);
  }

  /** 处理 @RequestBody 参数校验失败 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result<Void> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpServletRequest request,
      HttpServletResponse response) {
    return buildResult(ex, request, response);
  }

  /** 处理 @ModelAttribute 或表单参数校验失败 */
  @ExceptionHandler(BindException.class)
  public Result<Void> handleWebBindException(
      BindException ex, HttpServletRequest request, HttpServletResponse response) {
    return buildResult(ex, request, response);
  }

  /** 处理方法参数校验（如 @RequestParam @Valid） */
  @ExceptionHandler(ConstraintViolationException.class)
  public Result<Void> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request, HttpServletResponse response) {
    return buildResult(ex, request, response);
  }

  /** 通用构建校验错误响应 */
  private Result<Void> buildResult(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {
    Result<Void> result = resolveException(ex, request.getRequestURI());
    response.setStatus(result.getStatus());
    return result;
  }

  @Override
  protected List<ExceptionMapping> getExceptionMappings() {
    List<ExceptionMapping> mappings = new ArrayList<>();
    mappings.add(
        new ExceptionMapping(
            MethodArgumentNotValidException.class, BaseErrorCode.VALIDATION_FAILED));
    mappings.add(new ExceptionMapping(BindException.class, BaseErrorCode.VALIDATION_FAILED));
    mappings.add(
        new ExceptionMapping(ConstraintViolationException.class, BaseErrorCode.VALIDATION_FAILED));
    mappings.add(new ExceptionMapping(NoResourceFoundException.class, BaseErrorCode.NOT_FOUND));
    return mappings;
  }
}
