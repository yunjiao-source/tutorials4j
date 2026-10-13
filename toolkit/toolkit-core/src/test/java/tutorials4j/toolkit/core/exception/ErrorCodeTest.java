package tutorials4j.toolkit.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ErrorCode} 与 {@link CommonErrorCode} 单元测试。
 *
 * @author YunJiao Yang
 */
class ErrorCodeTest {

  @Test
  @DisplayName("错误码使用枚举名，消息非空")
  void shouldExposeCodeAndMessage() {
    assertThat(CommonErrorCode.values()).isNotEmpty();
    assertThat(CommonErrorCode.values())
        .allSatisfy(
            errorCode -> {
              assertThat(errorCode.getCode()).isEqualTo(errorCode.name());
              assertThat(errorCode.getMessage()).isNotBlank();
            });

    assertThat(CommonErrorCode.SYSTEM_EXCEPTION.getCode()).isEqualTo("SYSTEM_EXCEPTION");
    assertThat(CommonErrorCode.SYSTEM_EXCEPTION.getMessage()).isEqualTo("系统异常");
    assertThat(CommonErrorCode.LOCK_RELEASE_FAIL.getMessage()).isEqualTo("释放锁失败");
    assertThat(CommonErrorCode.ACQUIRE_LOCK_FAIL.getMessage()).isEqualTo("获取锁失败");
  }

  @Test
  @DisplayName("throwed 返回携带当前错误码的异常")
  void shouldCreateExceptionWithoutDetail() {
    ErrorCodeException exception = CommonErrorCode.SYSTEM_EXCEPTION.throwed();

    assertThat(exception.getErrorCode()).isSameAs(CommonErrorCode.SYSTEM_EXCEPTION);
    assertThat(exception.getMessage()).isEqualTo("系统异常");
    assertThat(exception.getDetail()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("throwed(message) 返回携带错误码与详情的异常")
  void shouldCreateExceptionWithDetail() {
    ErrorCodeException exception = CommonErrorCode.SYSTEM_EXCEPTION.throwed("数据库连接失败");

    assertThat(exception.getErrorCode()).isSameAs(CommonErrorCode.SYSTEM_EXCEPTION);
    assertThat(exception.getDetail()).isEqualTo("数据库连接失败");
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("throwed(message, cause) 返回携带错误码、详情与原因的异常")
  void shouldCreateExceptionWithDetailAndCause() {
    RuntimeException cause = new RuntimeException("root");

    ErrorCodeException exception = CommonErrorCode.SYSTEM_EXCEPTION.throwed("数据库连接失败", cause);

    assertThat(exception.getDetail()).isEqualTo("数据库连接失败");
    assertThat(exception.getCause()).isSameAs(cause);
  }

  @Test
  @DisplayName("throwed(cause) 返回携带错误码与原因的异常")
  void shouldCreateExceptionWithCause() {
    RuntimeException cause = new RuntimeException("root");

    ErrorCodeException exception = CommonErrorCode.SYSTEM_EXCEPTION.throwed(cause);

    assertThat(exception.getErrorCode()).isSameAs(CommonErrorCode.SYSTEM_EXCEPTION);
    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getDetail()).isNull();
  }

  @Test
  @DisplayName("自定义错误码实现同样可使用 throwed")
  void shouldSupportCustomErrorCode() {
    ErrorCode errorCode =
        new ErrorCode() {
          @Override
          public String getCode() {
            return "CUSTOM_ERROR";
          }

          @Override
          public String getMessage() {
            return "自定义错误";
          }
        };

    ErrorCodeException exception = errorCode.throwed("补充说明");

    assertThat(exception.getErrorCode()).isSameAs(errorCode);
    assertThat(exception.getMessage()).isEqualTo("自定义错误-补充说明");
    assertThat(exception.getDetail()).isEqualTo("补充说明");
    assertThat(exception.getErrorDetail().getCode()).isEqualTo("CUSTOM_ERROR");
  }
}
