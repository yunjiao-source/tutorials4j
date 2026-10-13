package tutorials4j.toolkit.core.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * {@link Lockable} 单元测试：默认前缀、键与时间配置、异常处理策略。
 *
 * @author YunJiao Yang
 */
class LockableTest {
  private Logger logger;
  private ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void setUp() {
    logger = (Logger) LoggerFactory.getLogger(Lockable.LogHolder.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
    appender.stop();
  }

  private List<ILoggingEvent> errorEvents() {
    return appender.list.stream().filter(event -> event.getLevel() == Level.ERROR).toList();
  }

  private static Lockable lockable() {
    return new Lockable() {
      @Override
      public String key(Map<String, String> params) {
        return "order:" + params.get("id");
      }

      @Override
      public Duration waitTime() {
        return Duration.ofSeconds(1);
      }

      @Override
      public Duration expireTime() {
        return Duration.ofSeconds(10);
      }
    };
  }

  @Test
  @DisplayName("默认前缀")
  void shouldUseDefaultPrefix() {
    assertThat(lockable().prefix()).isEqualTo("");
  }

  @Test
  @DisplayName("可重写前缀")
  void shouldAllowOverridingPrefix() {
    Lockable lockable =
        new Lockable() {
          @Override
          public String prefix() {
            return "custom:";
          }

          @Override
          public String key(Map<String, String> params) {
            return "key";
          }

          @Override
          public Duration waitTime() {
            return Duration.ZERO;
          }

          @Override
          public Duration expireTime() {
            return Duration.ofSeconds(1);
          }
        };

    assertThat(lockable.prefix()).isEqualTo("custom:");
  }

  @Test
  @DisplayName("按参数生成键，并暴露等待与过期时间")
  void shouldExposeKeyAndDurations() {
    Lockable lockable = lockable();

    assertThat(lockable.key(Map.of("id", "1"))).isEqualTo("order:1");
    assertThat(lockable.waitTime()).isEqualTo(Duration.ofSeconds(1));
    assertThat(lockable.expireTime()).isEqualTo(Duration.ofSeconds(10));
  }

  @Test
  @DisplayName("获取锁失败异常被静默忽略，不记录错误日志")
  void shouldSwallowAcquireLockFailException() {
    Lockable lockable = lockable();
    ErrorCodeException exception = CommonErrorCode.ACQUIRE_LOCK_FAIL.throwed();

    assertThatCode(() -> lockable.handleException(exception)).doesNotThrowAnyException();

    assertThat(errorEvents()).isEmpty();
  }

  @Test
  @DisplayName("其他异常被记录为错误日志且不外抛")
  void shouldLogOtherExceptions() {
    Lockable lockable = lockable();

    assertThatCode(() -> lockable.handleException(new UnauthorizedException()))
        .doesNotThrowAnyException();

    assertThat(errorEvents()).hasSize(1);
    assertThat(errorEvents().get(0).getFormattedMessage()).isEqualTo("加锁执行时异常");
  }

  @Test
  @DisplayName("非 ACQUIRE_LOCK_FAIL 的错误码异常同样记录错误日志")
  void shouldLogOtherErrorCodeExceptions() {
    Lockable lockable = lockable();

    lockable.handleException(CommonErrorCode.LOCK_RELEASE_FAIL.throwed());

    assertThat(errorEvents()).hasSize(1);
    assertThat(errorEvents().get(0).getThrowableProxy().getClassName())
        .isEqualTo(ErrorCodeException.class.getName());
  }
}
