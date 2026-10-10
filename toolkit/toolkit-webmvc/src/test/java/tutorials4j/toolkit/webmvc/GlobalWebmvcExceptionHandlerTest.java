package tutorials4j.toolkit.webmvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorDetail;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * {@link GlobalWebmvcExceptionHandler} 单元测试：Servlet MVC 场景下异常到 {@link ProblemDetail} 的转换。
 *
 * @author Yun Jiao
 */
class GlobalWebmvcExceptionHandlerTest {
  private static final String TRACE_ID = "0123456789abcdef0123456789abcdef";

  private final Tracer tracer = mock(Tracer.class);
  private final GlobalWebmvcExceptionHandler handler = new GlobalWebmvcExceptionHandler(tracer);

  private void stubTraceId(String traceId) {
    TraceContext traceContext = mock(TraceContext.class);
    when(traceContext.traceId()).thenReturn(traceId);
    Span span = mock(Span.class);
    when(span.context()).thenReturn(traceContext);
    when(tracer.currentSpan()).thenReturn(span);
  }

  private static ErrorDetail errorDetail(ProblemDetail problemDetail) {
    assertThat(problemDetail.getProperties()).containsKey("errors");
    return (ErrorDetail) problemDetail.getProperties().get("errors");
  }

  private static NoResourceFoundException noResourceFoundException() {
    return new NoResourceFoundException(HttpMethod.GET, "/missing", "静态资源不存在");
  }

  @Test
  @DisplayName("静态资源未找到：404 + 客户端异常标题")
  void shouldHandleNoResourceFoundException() {
    stubTraceId(TRACE_ID);
    NoResourceFoundException exception = noResourceFoundException();

    ProblemDetail problemDetail = handler.handleNoResourceFoundException(exception);

    assertThat(problemDetail.getStatus()).isEqualTo(404);
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(problemDetail.getDetail()).isEqualTo(exception.getMessage());

    ErrorDetail detail = errorDetail(problemDetail);
    assertThat(detail.getCode()).isEqualTo("NOT_FOUND");
    assertThat(detail.getClassName()).isEqualTo(NoResourceFoundException.class.getName());
    assertThat(detail.getTraceId()).isEqualTo(TRACE_ID);
    assertThat(detail.getStackTrace()).isNull();
  }

  @Test
  @DisplayName("静态资源未找到：无可用 Span 时链路 ID 为 null")
  void shouldReturnNullTraceIdWithoutSpan() {
    assertThat(tracer.currentSpan()).isNull();

    ProblemDetail problemDetail =
        handler.handleNoResourceFoundException(noResourceFoundException());

    assertThat(errorDetail(problemDetail).getTraceId()).isNull();
  }

  @Test
  @DisplayName("继承 HandleException，可直接处理错误码异常")
  void shouldHandleErrorCodeException() {
    assertThat(handler).isInstanceOf(HandleException.class);

    ProblemDetail problemDetail =
        handler.handleErrorCodeException(
            CommonErrorCode.SYSTEM_EXCEPTION.throwed("数据库连接失败"),
            HandleException.ErrorDetailCustomizer.EMPTY);

    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(problemDetail.getDetail()).isEqualTo("数据库连接失败");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("SYSTEM_EXCEPTION");
  }

  @Test
  @DisplayName("继承 HandleException，未知异常按 500 处理并写入堆栈")
  void shouldHandleUnexpectedException() {
    ProblemDetail problemDetail =
        handler.handleException(
            new IllegalStateException("boom"), HttpStatus.INTERNAL_SERVER_ERROR);

    assertThat(problemDetail.getStatus()).isEqualTo(500);
    assertThat(problemDetail.getTitle()).isEqualTo("系统端异常");

    ErrorDetail detail = errorDetail(problemDetail);
    assertThat(detail.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    assertThat(detail.getStackTrace()).isNotEmpty();
  }

  @Test
  @DisplayName("暴露 Tracer 供 HandleException 使用")
  void shouldExposeTracerAndLogger() {
    assertThat(handler.getTrace()).isSameAs(tracer);
  }

  @Test
  @DisplayName("声明为最高优先级的 RestControllerAdvice")
  void shouldBeHighestPrecedenceAdvice() {
    assertThat(GlobalWebmvcExceptionHandler.class.getAnnotation(RestControllerAdvice.class))
        .isNotNull();

    Order order = GlobalWebmvcExceptionHandler.class.getAnnotation(Order.class);
    assertThat(order).isNotNull();
    assertThat(order.value()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
  }
}
