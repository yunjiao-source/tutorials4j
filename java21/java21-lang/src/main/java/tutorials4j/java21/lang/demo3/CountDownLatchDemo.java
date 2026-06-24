package tutorials4j.java21.lang.demo3;

import java.util.concurrent.CountDownLatch;

/**
 * 主线程等待多个子线程完成任务
 *
 * @author Yun Jiao
 */
public class CountDownLatchDemo {
  public static void main(String[] args) throws InterruptedException {
    // 等待 2 个线程执行完
    CountDownLatch latch = new CountDownLatch(2);

    // 线程1
    new Thread(
            () -> {
              System.out.println("线程1执行中...");
              latch.countDown(); // 计数 -1
            })
        .start();

    // 线程2
    new Thread(
            () -> {
              System.out.println("线程2执行中...");
              latch.countDown(); // 计数 -1
            })
        .start();

    // 主线程等待（直到计数变为 0 才继续）
    latch.await();
    System.out.println("所有线程执行完毕，主线程继续！");
  }
}
