package tutorials4j.java21.lang.demo3;

import java.util.concurrent.CountDownLatch;

/**
 * 龟兔赛跑
 *
 * @author Yun Jiao
 */
public class CountDownLatchDemo2 {
  public static void main(String[] args) throws InterruptedException {
    // 计数器=1：发令枪信号
    CountDownLatch startLatch = new CountDownLatch(1);

    // 创建 5 个运动员线程
    for (int i = 1; i <= 5; i++) {
      final int index = i;
      new Thread(
              () -> {
                try {
                  System.out.println("运动员 " + index + " 已准备，等待发令枪...");
                  // 等待发令信号
                  startLatch.await();
                  System.out.println("运动员 " + index + " 开始起跑！");
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
              })
          .start();
    }

    // 主线程模拟发令准备
    Thread.sleep(2000);
    System.out.println("发令枪响！");
    // 计数器减为 0，所有线程同时执行
    startLatch.countDown();
  }
}
