package tutorials4j.java21.lang.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 接口调用的超时控制
 *
 * @author Yun Jiao
 */
public class OrTimeoutDemo {
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);

  // 模拟远程接口调用
  private static CompletableFuture<String> callRemoteApi(String url) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            // 模拟接口响应延迟
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          return url + " 响应结果";
        },
        executor);
  }

  public static void main(String[] args) {
    CompletableFuture<String> future =
        callRemoteApi("https://api.example.com")
            // 设置超时时间为1秒
            .orTimeout(1, TimeUnit.SECONDS)
            // 超时异常降级
            .exceptionally(
                ex -> {
                  System.out.println("接口调用超时：" + ex.getMessage());
                  return "默认降级结果";
                });
    System.out.println("最终结果：" + future.join());
    executor.shutdown();
  }
}
