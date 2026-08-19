package tutorials4j.framework.examples.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Redis 分布式锁调度示例配置。
 *
 * <p>通过多个定时方法并发调度演示任务，制造锁竞争与异常场景，用于验证 自动续期锁与固定租期锁在分布式环境下的行为。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ScheduleConfig {
  /** 自动续期锁演示任务。 */
  private final Demo1AutoRenewalTaskRunner demo1;

  /** 固定租期锁演示任务。 */
  private final Demo2FixedLeaseTaskRunner demo2;

  /** 定时执行自动续期锁演示任务，与 deom1_1 形成锁竞争。 */
  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void deom1() {
    demo1.run(null);
  }

  /** 定时执行自动续期锁演示任务，与 deom1 形成锁竞争。 */
  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void deom1_1() {
    demo1.run(null);
  }

  /** 定时执行固定租期锁演示任务，与 deom2_1 形成锁竞争。 */
  @Scheduled(initialDelay = 4000, fixedDelay = 3000)
  public void deom2() {
    demo2.run(null);
  }

  /** 定时执行固定租期锁演示任务，与 deom2 形成锁竞争。 */
  @Scheduled(initialDelay = 4000, fixedDelay = 3000)
  public void deom2_1() {
    demo2.run(null);
  }
}
