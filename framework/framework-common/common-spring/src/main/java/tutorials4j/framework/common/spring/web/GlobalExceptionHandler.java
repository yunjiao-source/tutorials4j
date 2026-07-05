package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import tutorials4j.framework.common.core.exception.ErrorCodeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
@Order(300)
public class GlobalExceptionHandler extends AbstractExceptionHandler {
  @ExceptionHandler(ErrorCodeException.class)
  public Result<Void> handleBaseException(
      ErrorCodeException ex, HttpServletRequest request, HttpServletResponse response) {
    Result<Void> result =
        ex.getResult()
            .path(request.getRequestURI())
            .traceId(MDC.get(DefaultConsts.HTTP_HEADER_TRACE_ID));

    response.setStatus(result.getStatus());

    log.warn("业务异常: {}", result);
    return result;
  }

  @ExceptionHandler(Exception.class)
  public Result<Void> handleOtherException(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {

    Result<Void> result = resolveException(ex, request.getRequestURI());
    response.setStatus(result.getStatus());

    log.error("系统异常", ex);
    return result;
  }

  @Override
  protected List<ExceptionMapping> getExceptionMappings() {
    List<ExceptionMapping> mappings = new ArrayList<>();
    mappings.add(
        new ExceptionMapping(
            UnsupportedOperationException.class, BaseErrorCode.INTERNAL_SERVER_ERROR));
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

    mappings.add(
        new ExceptionMapping(NullPointerException.class, BaseErrorCode.NULL_POINTER_EXCEPTION));
    mappings.add(
        new ExceptionMapping(TypeMismatchException.class, BaseErrorCode.TYPE_MISMATCH_EXCEPTION));

    return mappings;
  }
}
