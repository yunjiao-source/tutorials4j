package tutorials4j.java21.lang.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;

/**
 * 分支编排
 *
 * @author Yun Jiao
 */
public class BranchOrchestrationDemo {
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);

  static class User {
    private String userId;
    @Getter private int level;

    public User(String userId, int level) {
      this.userId = userId;
      this.level = level;
    }
  }

  // 普通会员权益查询
  private static CompletableFuture<String> queryNormalRights() {
    return CompletableFuture.supplyAsync(() -> "普通会员：无折扣", executor);
  }

  // 高级会员权益查询
  private static CompletableFuture<String> queryVipRights() {
    return CompletableFuture.supplyAsync(() -> "高级会员：9折优惠", executor);
  }

  public static void main(String[] args) {
    User user = new User("1001", 2); // 2级为高级会员
    // 分支编排：根据用户等级选择不同任务
    CompletableFuture<String> rightsFuture =
        user.getLevel() >= 2 ? queryVipRights() : queryNormalRights();
    System.out.println("会员权益：" + rightsFuture.join());
    executor.shutdown();
  }
}
