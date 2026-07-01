package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(300)
public class GlobalExceptionHandler extends AbstractExceptionHandler {
  @ExceptionHandler(BaseRuntimeException.class)
  public Result<Void> handleBaseException(
      BaseRuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    Result<Void> result =
        ex.getResult()
            .path(request.getRequestURI())
            .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));
    if (ex.getErrorCode().getFeedback().isSystemError()) {
      log.error("系统异常", ex);
    } else {
      log.warn("业务异常: {}", result);
    }
    response.setStatus(result.getStatus());
    return result;
  }

  @ExceptionHandler(Exception.class)
  public Result<Void> handleOtherException(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {

    Result<Void> result = resolveException(ex, request.getRequestURI());
    response.setStatus(result.getStatus());
    return result;
  }

  @Override
  protected List<ExceptionMapping> getExceptionMappings() {
    List<ExceptionMapping> mappings = new ArrayList<>();
    // ========== 4xx 客户端异常（具体类优先） ==========
    mappings.add(
        new ExceptionMapping(
            HttpMediaTypeNotSupportedException.class, BaseErrorCode.HTTP_MEDIA_TYPE_UNSUPPORTED));
    mappings.add(
        new ExceptionMapping(
            HttpMediaTypeNotAcceptableException.class, BaseErrorCode.NOT_ACCEPTABLE));
    mappings.add(
        new ExceptionMapping(
            MissingServletRequestParameterException.class,
            BaseErrorCode.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION));

    // ========== 5xx 服务器异常（具体类优先） ==========
    mappings.add(
        new ExceptionMapping(NullPointerException.class, BaseErrorCode.NULL_POINTER_EXCEPTION));
    mappings.add(
        new ExceptionMapping(
            IllegalArgumentException.class, BaseErrorCode.ILLEGAL_ARGUMENT_EXCEPTION));
    mappings.add(
        new ExceptionMapping(TypeMismatchException.class, BaseErrorCode.TYPE_MISMATCH_EXCEPTION));
    mappings.add(new ExceptionMapping(IOException.class, BaseErrorCode.IO_EXCEPTION));

    return mappings;
  }
}
