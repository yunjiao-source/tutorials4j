package tutorials4j.toolkit.core.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.ErrorDetail;
import tutorials4j.toolkit.core.exception.TooManyRequestException;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * {@link GlobalWebExceptionHandler} 单元测试：各类异常到 {@link ProblemDetail} 的转换。
 *
 * @author YunJiao Yang
 */
class GlobalWebExceptionHandlerTest {
  private static final String TRACE_ID = "0123456789abcdef0123456789abcdef";
  private static final Validator VALIDATOR =
      Validation.buildDefaultValidatorFactory().getValidator();

  private final Tracer tracer = mock(Tracer.class);
  private final GlobalWebExceptionHandler handler = new GlobalWebExceptionHandler(tracer);

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

  @Test
  @DisplayName("错误码异常：422 + 客户端异常标题 + 错误码/参数/链路 ID")
  void shouldHandleErrorCodeException() {
    stubTraceId(TRACE_ID);
    ErrorCodeException exception =
        CommonErrorCode.ACQUIRE_LOCK_FAIL.throwed("获取锁失败详情").param("lockKey", "order:1");

    ProblemDetail problemDetail = handler.handleErrorCodeException(exception);

    assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(problemDetail.getDetail()).isEqualTo("获取锁失败-获取锁失败详情");

    ErrorDetail detail = errorDetail(problemDetail);
    assertThat(detail.getCode()).isEqualTo("ACQUIRE_LOCK_FAIL");
    assertThat(detail.getClassName()).isEqualTo(ErrorCodeException.class.getName());
    assertThat(detail.getTraceId()).isEqualTo(TRACE_ID);
    assertThat(detail.getTimestamp()).isNotNull();
    assertThat(detail.getParams()).containsEntry("lockKey", "order:1");
    assertThat(detail.getStackTrace()).isNull();
  }

  @Test
  @DisplayName("错误码异常未携带详情时 detail 为空，仅保留错误码")
  void shouldHandleErrorCodeExceptionWithoutDetail() {
    ErrorCodeException exception = CommonErrorCode.SYSTEM_EXCEPTION.throwed();

    ProblemDetail problemDetail = handler.handleErrorCodeException(exception);

    assertThat(problemDetail.getDetail()).isEqualTo("系统异常");
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("SYSTEM_EXCEPTION");
  }

  @Test
  @DisplayName("限流异常：429 + 客户端异常标题")
  void shouldHandleTooManyRequestException() {
    ProblemDetail problemDetail =
        handler.handleTooManyRequestException(new TooManyRequestException());

    assertThat(problemDetail.getStatus()).isEqualTo(429);
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(problemDetail.getDetail()).isEqualTo("请求过于频繁, 请稍后重试");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("TOO_MANY_REQUESTS");
  }

  @Test
  @DisplayName("未登录异常：401 + 客户端异常标题")
  void shouldHandleUnauthorizedException() {
    ProblemDetail problemDetail = handler.handleUnauthorizedException(new UnauthorizedException());

    assertThat(problemDetail.getStatus()).isEqualTo(401);
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(problemDetail.getDetail()).isEqualTo("未登录系统");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("UNAUTHORIZED");
  }

  @Test
  @DisplayName("参数校验异常：400 + 校验异常标题 + 字段错误明细")
  void shouldHandleConstraintViolationException() {
    ProblemDetail problemDetail =
        handler.handleConstraintViolationException(
            new ConstraintViolationException(VALIDATOR.validate(new SampleForm())));

    assertThat(problemDetail.getStatus()).isEqualTo(400);
    assertThat(problemDetail.getTitle()).isEqualTo("校验异常");
    assertThat(errorDetail(problemDetail).getFieldErrors())
        .containsEntry("name", "不能为空")
        .containsEntry("age", "最小值为 1");
  }

  @Test
  @DisplayName("同一字段的多条校验消息会合并")
  void shouldMergeMessagesOfSameField() {
    ProblemDetail problemDetail =
        handler.handleConstraintViolationException(
            new ConstraintViolationException(VALIDATOR.validate(new DuplicatedForm())));

    String message = errorDetail(problemDetail).getFieldErrors().get("code");

    assertThat(message).contains("不能为空").contains("长度不足").contains(";");
  }

  @Test
  @DisplayName("绑定异常：400 + 校验异常标题 + 字段错误明细，缺失消息回退为无效值")
  void shouldHandleBindException() {
    BindException exception = new BindException(new SampleForm(), "sampleForm");
    exception.rejectValue("name", "NotBlank", "不能为空");
    exception.rejectValue("age", "Min", null);

    ProblemDetail problemDetail = handler.handleBindException(exception);

    assertThat(problemDetail.getStatus()).isEqualTo(400);
    assertThat(problemDetail.getTitle()).isEqualTo("校验异常");
    assertThat(errorDetail(problemDetail).getFieldErrors())
        .containsEntry("name", "不能为空")
        .containsEntry("age", "无效值");
  }

  @Test
  @DisplayName("同一字段的重复绑定错误只保留一条")
  void shouldKeepSingleMessageForDuplicatedBindError() {
    BindException exception = new BindException(new SampleForm(), "sampleForm");
    exception.rejectValue("name", "NotBlank", "不能为空");
    exception.rejectValue("name", "Size", "长度不足");

    ProblemDetail problemDetail = handler.handleBindException(exception);

    Map<String, String> fieldErrors = errorDetail(problemDetail).getFieldErrors();
    assertThat(fieldErrors).containsOnlyKeys("name");
    assertThat(fieldErrors.get("name")).isIn("不能为空", "长度不足");
  }

  @Test
  @DisplayName("未知异常：500 + 系统端异常标题 + 堆栈写入 ErrorDetail")
  void shouldHandleUnexpectedException() {
    ProblemDetail problemDetail = handler.handleException(new IllegalStateException("boom"));

    assertThat(problemDetail.getStatus()).isEqualTo(500);
    assertThat(problemDetail.getTitle()).isEqualTo("系统端异常");
    assertThat(problemDetail.getDetail()).isEqualTo("boom");

    ErrorDetail detail = errorDetail(problemDetail);
    assertThat(detail.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    assertThat(detail.getClassName()).isEqualTo(IllegalStateException.class.getName());
    assertThat(detail.getStackTrace()).isNotEmpty();
  }

  @Test
  @DisplayName("自定义定制器可覆盖 ErrorDetail")
  void shouldApplyErrorDetailCustomizer() {
    ProblemDetail problemDetail =
        handler.handleException(
            new IllegalStateException("boom"),
            HttpStatus.CONFLICT,
            detail -> detail.setCode("CUSTOM"));

    assertThat(problemDetail.getStatus()).isEqualTo(409);
    assertThat(problemDetail.getTitle()).isEqualTo("客户端异常");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("CUSTOM");
    assertThat(errorDetail(problemDetail).getStackTrace()).isNull();
  }

  @Test
  @DisplayName("非 4xx/5xx 状态码归入其他异常，且不写入堆栈")
  void shouldHandleNonErrorStatus() {
    ProblemDetail problemDetail =
        handler.handleException(new IllegalStateException("boom"), HttpStatus.OK);

    assertThat(problemDetail.getTitle()).isEqualTo("其他异常");
    assertThat(errorDetail(problemDetail).getCode()).isEqualTo("OK");
    assertThat(errorDetail(problemDetail).getStackTrace()).isNull();
  }

  @Test
  @DisplayName("无可用 Span 时链路 ID 为 null")
  void shouldReturnNullTraceIdWithoutSpan() {
    assertThat(tracer.currentSpan()).isNull();

    ProblemDetail problemDetail =
        handler.handleErrorCodeException(CommonErrorCode.SYSTEM_EXCEPTION.throwed());

    assertThat(errorDetail(problemDetail).getTraceId()).isNull();
  }

  @Test
  @DisplayName("暴露 Tracer 供子类使用")
  void shouldExposeTracerAndLogger() {
    assertThat(handler.getTrace()).isSameAs(tracer);
  }

  static class SampleForm {
    @NotBlank(message = "不能为空")
    private String name;

    @Min(value = 1, message = "最小值为 1")
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

  static class DuplicatedForm {
    @NotBlank(message = "不能为空")
    @Size(min = 5, message = "长度不足")
    private String code = "";
  }
}
