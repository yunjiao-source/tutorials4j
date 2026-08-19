package tutorials4j.framework.examples.redisson;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 锁异常演示调度配置。
 *
 * <p>通过多个定时任务并发触发同一示例任务，制造锁竞争与锁异常场景，用于演示分布式锁的各种行为。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ScheduleConfig {
  private final Demo1AutoRenewalBlockTaskRunner demo1;
  private final Demo2AutoRenewalReentrantTaskRunner demo2;
  private final Demo3FixedLeaseBlockTaskRunner demo3;
  private final Demo4FixedLeaseReentrantTaskRunner demo4;

  /** 定时触发 demo1 任务（自动续期阻塞锁）。 */
  @Scheduled(initialDelay = 3000, fixedDelay = 6000)
  public void deom1() {
    demo1.run(null);
  }

  /** 定时触发 demo1 任务，与 {@link #deom1()} 并发制造锁竞争。 */
  @Scheduled(initialDelay = 5000, fixedDelay = 6000)
  public void deom1_1() {
    demo1.run(null);
  }

  /** 定时触发 demo2 任务（自动续期可重入锁）。 */
  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom2() {
    demo2.run(null);
  }

  /** 定时触发 demo2 任务，与 {@link #deom2()} 并发制造锁竞争。 */
  @Scheduled(initialDelay = 5000, fixedDelay = 6000)
  public void deom2_1() {
    demo2.run(null);
  }

  /** 定时触发 demo3 任务（固定租期阻塞锁）。 */
  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom3() {
    demo3.run(null);
  }

  /** 定时触发 demo3 任务，与 {@link #deom3()} 并发制造锁竞争。 */
  @Scheduled(initialDelay = 6000, fixedDelay = 6000)
  public void deom3_1() {
    demo3.run(null);
  }

  /** 定时触发 demo4 任务（固定租期可重入锁）。 */
  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom4() {
    demo4.run(null);
  }

  /** 定时触发 demo4 任务，与 {@link #deom4()} 并发制造锁竞争。 */
  @Scheduled(initialDelay = 7000, fixedDelay = 6000)
  public void deom4_1() {
    demo4.run(null);
  }
}
