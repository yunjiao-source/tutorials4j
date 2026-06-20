package tutorials4j.java21.lang.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 缓存查询的超时降级
 *
 * @author Yun Jiao
 */
public class CompleteOnTimeoutDemo {
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);

  // 模拟缓存查询
  private static CompletableFuture<String> queryCache(String key) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            // 模拟缓存服务延迟
            Thread.sleep(1500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          return "缓存值：" + key;
        },
        executor);
  }

  public static void main(String[] args) {
    CompletableFuture<String> future =
        queryCache("user_1001")
            // 超时1秒返回默认值
            .completeOnTimeout("缓存降级值", 1, TimeUnit.SECONDS);
    System.out.println("最终结果：" + future.join());
    executor.shutdown();
  }
}
