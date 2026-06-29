package tutorials4j.framework.examples.redisson;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 制造锁异常
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

  @Scheduled(initialDelay = 3000, fixedDelay = 6000)
  public void deom1() {
    demo1.run(null);
  }

  @Scheduled(initialDelay = 5000, fixedDelay = 6000)
  public void deom1_1() {
    demo1.run(null);
  }

  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom2() {
    demo2.run(null);
  }

  @Scheduled(initialDelay = 5000, fixedDelay = 6000)
  public void deom2_1() {
    demo2.run(null);
  }

  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom3() {
    demo3.run(null);
  }

  @Scheduled(initialDelay = 6000, fixedDelay = 6000)
  public void deom3_1() {
    demo3.run(null);
  }

  @Scheduled(initialDelay = 4000, fixedDelay = 6000)
  public void deom4() {
    demo4.run(null);
  }

  @Scheduled(initialDelay = 7000, fixedDelay = 6000)
  public void deom4_1() {
    demo4.run(null);
  }
}
