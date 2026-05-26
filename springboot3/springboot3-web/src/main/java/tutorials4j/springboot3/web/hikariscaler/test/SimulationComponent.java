package tutorials4j.springboot3.web.hikariscaler.test;

/**
 * 连接池性能测试组件
 *
 * @author Yun Jiao
 */
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.common.jpa.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimulationComponent {
  private final UserService userService;

  private final ExecutorService executorService;

  public void simulateHighLoad(int concurrentRequests, int durationSeconds) {
    log.info("开始模拟高并发场景: {} 并发, {} 秒", concurrentRequests, durationSeconds);

    CountDownLatch latch = new CountDownLatch(concurrentRequests);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(durationSeconds);

    for (int i = 0; i < concurrentRequests; i++) {
      executorService.submit(
          () -> {
            try {
              System.out.println(Thread.currentThread().getName());
              while (System.currentTimeMillis() < endTime) {
                try {

                  successCount.incrementAndGet();
                } catch (Exception e) {
                  failCount.incrementAndGet();
                }

                // 模拟随机间隔
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              latch.countDown();
            }
          });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    log.info("模拟完成: 成功={}, 失败={}", successCount.get(), failCount.get());
  }
}
