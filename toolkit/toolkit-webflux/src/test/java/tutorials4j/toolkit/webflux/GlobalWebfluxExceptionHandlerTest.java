package tutorials4j.toolkit.webflux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorDetail;
import tutorials4j.toolkit.core.web.HandleException;

/**
 * {@link GlobalWebfluxExceptionHandler} 单元测试：WebFlux 场景下异常到 {@link ProblemDetail} 的转换。
 *
 * @author Yun Jiao
 */
class GlobalWebfluxExceptionHandlerTest {
  private static final String TRACE_ID = "0123456789abcdef0123456789abcdef";

  private final Tracer tracer = mock(Tracer.class);
  private final GlobalWebfluxExceptionHandler handler = new GlobalWebfluxExceptionHandler(tracer);
  private final ServerWebExchange exchange = mock(ServerWebExchange.class);

  static class Form {
    private String name;
    private int age;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }

  static class SampleController {
    public void handle(Form form) {}
  }

  private void stubTraceId(String traceId) {
    TraceContext traceContext = mock(TraceContext.class);
    when(traceContext.traceId()).thenReturn(traceId);
    Span span = mock(Span.class);
    when(span.context()).thenReturn(traceContext);
    when(tracer.currentSpan()).thenReturn(span);
  }

  private static WebExchangeBindException bindException() {
    try {
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getMethod("handle", Form.class), 0);
      return new WebExchangeBindException(
          parameter, new BeanPropertyBindingResult(new Form(), "form"));
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  private static ErrorDetail errorDetail(ProblemDetail problemDetail) {
    assertThat(problemDetail.getProperties()).containsKey("errors");
    return (ErrorDetail) problemDetail.getProperties().get("errors");
  }

  @Test
  @DisplayName("静态资源未找到：404 + 客户端异常标题")
  void shouldHandleNoResourceFoundException() {
    stubTraceId(TRACE_ID);
    NoResourceFoundException exception =
        new NoResourceFoundException(URI.create("/missing"), "/missing");

    ProblemDetail problemDetail = handler.handleNoResourceFoundException(exception, exchange);

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
  @DisplayName("绑定异常：422 + 绑定异常标题 + 字段错误明细，缺失消息回退为无效值")
  void shouldHandleWebExchangeBindException() {
    WebExchangeBindException exception = bindException();
    exception.rejectValue("name", "NotBlank", "不能为空");
    exception.rejectValue("age", "Min", null);

    ProblemDetail problemDetail = handler.handleWebExchangeBindException(exception, exchange);

    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(problemDetail.getTitle()).isEqualTo("绑定异常");
    assertThat(errorDetail(problemDetail).getFieldErrors())
        .containsEntry("name", "不能为空")
        .containsEntry("age", "无效值");
  }

  @Test
  @DisplayName("绑定异常：同一字段的重复错误只保留一条")
  void shouldKeepSingleMessageForDuplicatedFieldError() {
    WebExchangeBindException exception = bindException();
    exception.rejectValue("name", "NotBlank", "不能为空");
    exception.rejectValue("name", "Size", "长度不足");

    ProblemDetail problemDetail = handler.handleWebExchangeBindException(exception, exchange);

    Map<String, String> fieldErrors = errorDetail(problemDetail).getFieldErrors();
    assertThat(fieldErrors).containsOnlyKeys("name");
    assertThat(fieldErrors.get("name")).isIn("不能为空", "长度不足");
  }

  @Test
  @DisplayName("绑定异常：全局错误不写入字段错误明细")
  void shouldIgnoreGlobalErrors() {
    WebExchangeBindException exception = bindException();
    exception.reject("globalError", "全局错误");

    ProblemDetail problemDetail = handler.handleWebExchangeBindException(exception, exchange);

    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(errorDetail(problemDetail).getFieldErrors()).isEmpty();
  }

  @Test
  @DisplayName("绑定异常：无可用 Span 时链路 ID 为 null")
  void shouldReturnNullTraceIdWithoutSpan() {
    assertThat(tracer.currentSpan()).isNull();

    ProblemDetail problemDetail = handler.handleWebExchangeBindException(bindException(), exchange);

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
  @DisplayName("暴露 Tracer 与 Logger 供 HandleException 使用")
  void shouldExposeTracerAndLogger() {
    assertThat(handler.getTrace()).isSameAs(tracer);
    assertThat(handler.getLog()).isNotNull();
  }

  @Test
  @DisplayName("声明为最高优先级的 RestControllerAdvice")
  void shouldBeHighestPrecedenceAdvice() {
    assertThat(GlobalWebfluxExceptionHandler.class.getAnnotation(RestControllerAdvice.class))
        .isNotNull();

    Order order = GlobalWebfluxExceptionHandler.class.getAnnotation(Order.class);
    assertThat(order).isNotNull();
    assertThat(order.value()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
  }
}
