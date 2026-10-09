package tutorials4j.toolkit.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ErrorCodeException} 单元测试：错误码、详情、参数与 ErrorDetail 转换。
 *
 * @author Yun Jiao
 */
class ErrorCodeExceptionTest {

  @Test
  @DisplayName("仅携带错误码：消息取自错误码，详情与原因均为 null，参数为空")
  void shouldUseErrorCodeMessageByDefault() {
    ErrorCodeException exception = new ErrorCodeException(CommonErrorCode.SYSTEM_EXCEPTION);

    assertThat(exception.getErrorCode()).isSameAs(CommonErrorCode.SYSTEM_EXCEPTION);
    assertThat(exception.getMessage()).isEqualTo("系统异常");
    assertThat(exception.getDetail()).isNull();
    assertThat(exception.getCause()).isNull();
    assertThat(exception.getParams()).isEmpty();
  }

  @Test
  @DisplayName("携带错误码与详情")
  void shouldKeepDetail() {
    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.LOCK_RELEASE_FAIL, "释放锁失败详情");

    assertThat(exception.getDetail()).isEqualTo("释放锁失败详情");
    assertThat(exception.getCause()).isNull();
    assertThat(exception.getMessage()).isEqualTo("释放锁失败");
  }

  @Test
  @DisplayName("携带错误码、详情与原因")
  void shouldKeepDetailAndCause() {
    RuntimeException cause = new RuntimeException("root");

    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.LOCK_RELEASE_FAIL, "释放锁失败详情", cause);

    assertThat(exception.getDetail()).isEqualTo("释放锁失败详情");
    assertThat(exception.getCause()).isSameAs(cause);
  }

  @Test
  @DisplayName("携带错误码与原因：详情为 null")
  void shouldKeepCauseOnly() {
    RuntimeException cause = new RuntimeException("root");

    ErrorCodeException exception = new ErrorCodeException(CommonErrorCode.ACQUIRE_LOCK_FAIL, cause);

    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getDetail()).isNull();
  }

  @Test
  @DisplayName("构造时复制参数映射，之后修改原映射不影响异常")
  void shouldCopyParamMap() {
    Map<String, Object> source = new LinkedHashMap<>();
    source.put("lockKey", "order:1");

    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.ACQUIRE_LOCK_FAIL, null, null, source);
    source.put("later", "value");

    assertThat(exception.getParams()).containsOnlyKeys("lockKey");
  }

  @Test
  @DisplayName("参数映射为 null 时参数为空")
  void shouldTolerateNullParamMap() {
    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.ACQUIRE_LOCK_FAIL, null, null, null);

    assertThat(exception.getParams()).isEmpty();
  }

  @Test
  @DisplayName("param 支持链式添加，且保持插入顺序")
  void shouldAddParamsChainably() {
    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.ACQUIRE_LOCK_FAIL)
            .param("lockKey", "order:1")
            .param("waitTime", "1s")
            .param("lockKey", "order:2");

    assertThat(exception.getParams()).containsOnlyKeys("lockKey", "waitTime");
    assertThat(exception.getParams().get("lockKey")).isEqualTo("order:2");
    assertThat(exception.getParams().keySet()).containsExactly("lockKey", "waitTime");
  }

  @Test
  @DisplayName("转换为 ErrorDetail：包含错误码、类名、参数与时间戳")
  void shouldConvertToErrorDetail() {
    ErrorCodeException exception =
        new ErrorCodeException(CommonErrorCode.ACQUIRE_LOCK_FAIL, "获取锁失败")
            .param("lockKey", "order:1");

    ErrorDetail detail = exception.getErrorDetail();

    assertThat(detail.getCode()).isEqualTo("ACQUIRE_LOCK_FAIL");
    assertThat(detail.getClassName()).isEqualTo(ErrorCodeException.class.getName());
    assertThat(detail.getParams()).containsEntry("lockKey", "order:1");
    assertThat(detail.getFieldErrors()).isEmpty();
    assertThat(detail.getTimestamp()).isNotNull();
    assertThat(detail.getTraceId()).isNull();
  }

  @Test
  @DisplayName("子类转换为 ErrorDetail 时类名为子类全限定名")
  void shouldUseSubclassNameInErrorDetail() {
    ErrorCodeException exception = new CustomErrorCodeException(CommonErrorCode.SYSTEM_EXCEPTION);

    assertThat(exception.getErrorDetail().getClassName())
        .isEqualTo(CustomErrorCodeException.class.getName());
  }

  static class CustomErrorCodeException extends ErrorCodeException {
    CustomErrorCodeException(ErrorCode errorCode) {
      super(errorCode);
    }
  }
}
