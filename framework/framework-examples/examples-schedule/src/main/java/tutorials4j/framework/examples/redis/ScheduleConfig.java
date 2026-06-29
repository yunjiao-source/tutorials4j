package tutorials4j.framework.examples.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 制造锁异常,
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ScheduleConfig {
  private final Demo1AutoRenewalTaskRunner demo1;
  private final Demo2FixedLeaseTaskRunner demo2;

  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void deom1() {
    demo1.run(null);
  }

  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void deom1_1() {
    demo1.run(null);
  }

  @Scheduled(initialDelay = 4000, fixedDelay = 3000)
  public void deom2() {
    demo2.run(null);
  }

  @Scheduled(initialDelay = 4000, fixedDelay = 3000)
  public void deom2_1() {
    demo2.run(null);
  }
}
