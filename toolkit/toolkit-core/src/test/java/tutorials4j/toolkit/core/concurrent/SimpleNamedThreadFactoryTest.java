package tutorials4j.toolkit.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link SimpleNamedThreadFactory} 单元测试。
 *
 * @author Yun Jiao
 */
class SimpleNamedThreadFactoryTest {

  @Test
  @DisplayName("线程名称 = 前缀 + 短横线")
  void shouldNameThreadWithPrefix() {
    Thread thread = new SimpleNamedThreadFactory("biz-pool-", false).newThread(() -> {});

    assertThat(thread.getName()).isEqualTo("biz-pool--");
  }

  @Test
  @DisplayName("同一工厂创建的线程名称相同，不追加自增序号")
  void shouldNotAppendSequenceNumber() {
    SimpleNamedThreadFactory factory = new SimpleNamedThreadFactory("biz-pool-", false);

    Thread first = factory.newThread(() -> {});
    Thread second = factory.newThread(() -> {});

    assertThat(first).isNotSameAs(second);
    assertThat(second.getName()).isEqualTo(first.getName());
  }

  @Test
  @DisplayName("daemon 参数决定线程是否为守护线程")
  void shouldConfigureDaemonFlag() {
    assertThat(new SimpleNamedThreadFactory("test-", true).newThread(() -> {}).isDaemon()).isTrue();
    assertThat(new SimpleNamedThreadFactory("test-", false).newThread(() -> {}).isDaemon())
        .isFalse();
  }

  @Test
  @DisplayName("传入的 Runnable 作为线程任务执行")
  void shouldUseGivenRunnable() throws InterruptedException {
    AtomicBoolean executed = new AtomicBoolean(false);

    Thread thread =
        new SimpleNamedThreadFactory("test-", false).newThread(() -> executed.set(true));
    thread.start();
    thread.join(5_000);

    assertThat(executed).isTrue();
  }
}
