package tutorials4j.toolkit.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link BaseException} 单元测试：受检异常基类的构造方式。
 *
 * @author Yun Jiao
 */
class BaseExceptionTest {

  @Test
  @DisplayName("属于受检异常（继承 Exception，不是 RuntimeException）")
  void shouldBeCheckedException() {
    assertThat(BaseException.class).isAssignableTo(Exception.class);
    assertThat(RuntimeException.class.isAssignableFrom(BaseException.class)).isFalse();
  }

  @Test
  @DisplayName("无参构造：消息与原因均为 null")
  void shouldSupportNoArgConstructor() {
    BaseException exception = new BaseException();

    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("带消息构造")
  void shouldSupportMessageConstructor() {
    BaseException exception = new BaseException("业务异常");

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isNull();
  }

  @Test
  @DisplayName("带消息与原因构造")
  void shouldSupportMessageAndCauseConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseException exception = new BaseException("业务异常", cause);

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isSameAs(cause);
  }

  @Test
  @DisplayName("带原因构造：消息为原因的描述")
  void shouldSupportCauseConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseException exception = new BaseException(cause);

    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getMessage()).isEqualTo(cause.toString());
  }

  @Test
  @DisplayName("完整参数构造：可关闭消息抑制与堆栈写入")
  void shouldSupportFullConstructor() {
    RuntimeException cause = new RuntimeException("root");

    BaseException exception = new BaseException("业务异常", cause, false, false);

    assertThat(exception.getMessage()).isEqualTo("业务异常");
    assertThat(exception.getCause()).isSameAs(cause);
    assertThat(exception.getStackTrace()).isEmpty();

    exception.addSuppressed(new IllegalStateException("suppressed"));
    assertThat(exception.getSuppressed()).isEmpty();
  }

  @Test
  @DisplayName("完整参数构造：开启抑制时 suppressed 生效")
  void shouldKeepSuppressedWhenEnabled() {
    BaseException exception = new BaseException("业务异常", null, true, true);

    exception.addSuppressed(new IllegalStateException("suppressed"));

    assertThat(exception.getSuppressed()).hasSize(1);
    assertThat(exception.getStackTrace()).isNotEmpty();
  }
}
