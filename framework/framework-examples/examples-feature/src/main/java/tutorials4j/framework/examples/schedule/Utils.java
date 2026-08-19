package tutorials4j.framework.examples.schedule;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务示例使用的工具类，提供模拟任务执行耗时的随机休眠方法。
 *
 * @author Yun Jiao
 */
public class Utils {
  /**
   * 随机休眠 0-10 秒，模拟任务执行耗时。
   *
   * @throws RuntimeException 休眠过程被中断时抛出
   */
  public static void sleep() {
    long milli = ThreadLocalRandom.current().nextInt(10000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
