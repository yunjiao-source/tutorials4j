package tutorials4j.java21.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 远程接口的固定次数重试
 *
 * @author Yun Jiao
 */
public class RetryDemo1 {
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);
  // 最大重试次数
  private static final int MAX_RETRY = 3;

  // 模拟远程接口调用，随机失败
  private static CompletableFuture<String> callApi(String url) {
    return CompletableFuture.supplyAsync(
        () -> {
          System.out.println("调用接口：" + url);
          // 模拟50%概率失败
          if (Math.random() > 0.4) {
            throw new RuntimeException("接口调用失败");
          }
          return url + " 响应成功";
        },
        executor);
  }

  // 重试方法：递归实现
  private static CompletableFuture<String> callApiWithRetry(String url, int retryCount) {
    return callApi(url)
        .exceptionallyCompose(
            ex -> {
              if (retryCount >= MAX_RETRY) {
                System.out.println("达到最大重试次数，降级处理");
                return CompletableFuture.completedFuture("接口降级结果");
              }
              System.out.println("重试第" + (retryCount + 1) + "次");
              // 延迟 1 秒后执行下一次重试
              return CompletableFuture.supplyAsync(
                      () -> null, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS))
                  .thenCompose(ignored -> callApiWithRetry(url, retryCount + 1));
            });
  }

  public static void main(String[] args) {
    CompletableFuture<String> future = callApiWithRetry("https://api.example.com", 0);
    System.out.println("最终结果：" + future.join());
    executor.shutdown();
  }
}
