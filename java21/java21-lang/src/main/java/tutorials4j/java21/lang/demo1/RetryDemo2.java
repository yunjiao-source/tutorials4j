package tutorials4j.java21.lang.demo1;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避重试
 *
 * @author Yun Jiao
 */
public class RetryDemo2 {
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);
  private static final int MAX_RETRY = 3;
  // 初始重试间隔
  private static final long INITIAL_DELAY = 1000;

  private static CompletableFuture<String> callApi(String url) {
    return CompletableFuture.supplyAsync(
        () -> {
          System.out.println("调用接口：" + url);
          if (Math.random() > 0.4) {
            throw new RuntimeException("接口调用失败");
          }
          return url + " 响应成功";
        },
        executor);
  }

  // 指数退避重试
  private static CompletableFuture<String> callApiWithExponentialRetry(String url, int retryCount) {
    return callApi(url)
        .exceptionallyCompose(
            ex -> {
              if (retryCount >= MAX_RETRY) {
                System.out.println("达到最大重试次数，降级处理");
                return CompletableFuture.completedFuture("接口降级结果");
              }
              System.out.println("重试第" + (retryCount + 1) + "次");
              // 指数退避：间隔 = 初始间隔 * (2^重试次数)
              long delay = INITIAL_DELAY * (1L << retryCount);
              System.out.println("等待" + delay + "ms后重试");
              return CompletableFuture.supplyAsync(
                      () -> null, CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS))
                  .thenCompose(ignored -> callApiWithExponentialRetry(url, retryCount + 1));
            });
  }

  public static void main(String[] args) {
    CompletableFuture<String> future = callApiWithExponentialRetry("https://api.example.com", 0);
    System.out.println("最终结果：" + future.join());
    executor.shutdown();
  }
}
