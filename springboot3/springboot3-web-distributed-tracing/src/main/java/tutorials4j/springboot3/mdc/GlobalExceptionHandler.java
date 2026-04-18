package tutorials4j.springboot3.mdc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 全局异常处理中的MDC
 *
 * @author Yun Jiao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // 获取当前MDC中的traceId
        String traceId = MDC.get(TraceConstants.TRACE_ID);

        // 记录异常日志，会自动包含MDC中的traceId
        log.error("Global exception caught: {}", e.getMessage(), e);

        // 创建错误响应对象
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTraceId(traceId);  // 设置traceId到响应体
        errorResponse.setPath(getCurrentRequestPath());  // 可选：设置请求路径

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getCurrentRequestPath() {
        try {
            return jakarta.servlet.http.HttpServletRequest.class
                    .cast(org.springframework.web.context.request.RequestContextHolder
                            .getRequestAttributes())
                    .getRequestURI();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
