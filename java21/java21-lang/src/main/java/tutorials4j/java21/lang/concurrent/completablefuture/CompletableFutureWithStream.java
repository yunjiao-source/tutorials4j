package tutorials4j.java21.lang.concurrent.completablefuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 批量数据并行处理
 *
 * @author Yun Jiao
 */
public class CompletableFutureWithStream {
  // 自定义并行线程池
  private static final ExecutorService PARALLEL_POOL =
      new ThreadPoolExecutor(
          5,
          10,
          60L,
          TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(100),
          BasicThreadFactory.builder().namingPattern("parallel-thread-%d").build(),
          new ThreadPoolExecutor.CallerRunsPolicy());

  // 模拟查询单个用户信息
  private static CompletableFuture<String> queryUser(String userId) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          return "用户" + userId + "信息";
        },
        PARALLEL_POOL);
  }

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    // 构建用户ID列表
    List<String> userIds = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      userIds.add("" + i);
    }
    // 并行查询所有用户信息
    List<CompletableFuture<String>> futureList =
        userIds.stream()
            .map(
                userId ->
                    queryUser(userId)
                        .orTimeout(1, TimeUnit.SECONDS)
                        .exceptionally(ex -> "超时: 未获取到用户信息"))
            .toList();
    // 等待所有任务完成并聚合结果
    List<String> userList = futureList.stream().map(CompletableFuture::join).toList();
    // 输出结果
    System.out.println("用户信息列表：");
    userList.forEach(System.out::println);
    System.out.println("总耗时：" + (System.currentTimeMillis() - start) + "ms");
    PARALLEL_POOL.shutdown();
  }
}
