package tutorials4j.springboot3.web.restresponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import tutorials4j.springboot3.web.Result;

/**
 * 全局异常处理
 *
 * @author Yun Jiao
 */
@RestControllerAdvice // 作用于所有@RestController注解的控制器
@Slf4j // 日志记录
public class GlobalExceptionHandler {
  // 1. 捕获业务异常（自定义异常，后续可扩展）
  @ExceptionHandler(BusinessException.class)
  public Result<?> handleBusinessException(BusinessException e) {
    log.error("业务异常：{}", e.getMessage(), e);
    // 若自定义异常有状态码，使用自定义状态码；否则使用默认业务异常状态码
    return Result.fail(
        e.getCode() != null ? e.getCode() : ResultCode.BUSINESS_ERROR.getCode(), e.getMessage());
  }

  // 2. 捕获参数校验异常（如@NotNull、@NotBlank等注解校验失败）
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("参数校验异常：{}", e.getMessage(), e);
    // 获取校验失败的第一条提示信息
    String errorMsg = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    return Result.fail(ResultCode.BAD_REQUEST, errorMsg);
  }

  // 3. 捕获404异常（资源不存在）
  @ExceptionHandler(NoHandlerFoundException.class)
  public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
    log.error("资源不存在：{}", e.getMessage(), e);
    return Result.fail(ResultCode.NOT_FOUND);
  }

  // 4. 捕获其他所有未定义的异常（兜底异常）
  @ExceptionHandler(Exception.class)
  public Result<?> handleException(Exception e) {
    log.error("服务器内部异常：{}", e.getMessage(), e);
    // 生产环境可返回通用提示，避免暴露异常详情；开发环境可返回具体异常信息
    return Result.error("服务器内部异常，请联系管理员");
    // 开发环境可用：return Result.error(e.getMessage());
  }
}
