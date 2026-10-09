package tutorials4j.toolkit.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link BaseRuntimeException} 单元测试：非受检异常基类的构造方式。
 *
 * @author Yun Jiao
 */
class BaseRuntimeExceptionTest {

  @Test
  @DisplayName("属于非受检异常（继承 RuntimeException）")
  void shouldBeUncheckedException() {
    assertThat(BaseRuntimeException.class).isAssignableTo(RuntimeException.class);
  }

  @Test
  @DisplayName("无参构造：消息与原因均为 null")
  void shouldSupportNoArgConstructor() {
    BaseRuntimeException exception = new BaseRuntimeException();

    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("带消息构造")
  void shouldSupportMessageConstructor() {
    BaseRuntimeException exception = new BaseRuntimeException("业务异常");

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("带消息与原因构造")
  void shouldSupportMessageAndCauseConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseRuntimeException exception = new BaseRuntimeException("业务异常", cause);

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isSameAs(cause);
  }

  @Test
  @DisplayName("带原因构造：消息为原因的描述")
  void shouldSupportCauseConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseRuntimeException exception = new BaseRuntimeException(cause);

    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getMessage()).isEqualTo(cause.toString());
  }

  @Test
  @DisplayName("完整参数构造：可关闭消息抑制与堆栈写入")
  void shouldSupportFullConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseRuntimeException exception = new BaseRuntimeException("业务异常", cause, false, false);

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getStackTrace()).isEmpty();

    exception.addSuppressed(new IllegalStateException("suppressed"));
    assertThat(exception.getSuppressed()).isEmpty();
  }

  @Test
  @DisplayName("可作为自定义业务异常的基类，保留原因链")
  void shouldBeExtendable() {
    BaseRuntimeException exception = new CustomException(new IllegalStateException("root"));

    assertThat(exception).isInstanceOf(BaseRuntimeException.class);
    assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class);
  }

  static class CustomException extends BaseRuntimeException {
    CustomException(Throwable cause) {
      super("自定义异常", cause);
    }
  }
}
