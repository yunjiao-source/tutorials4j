package tutorials4j.springboot3.demo2;

import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 订单业务分布式锁实现 */
@Slf4j
@Component
public class OrderLockService extends AbstractLockTemplate {

  public void exec(String orderId) {}

  @Override
  protected void initLockParam(LockContext context) {
    // 自定义业务锁key：订单号唯一标识
    context.setLockKey("lock:order:20260608001");
    // 自定义超时时间：30秒
    context.setTimeout(30000);
    // 初始化线程信息
    context.setHoldThread(Thread.currentThread().getName());
  }

  @Override
  protected void doBusiness() {
    log.info("执行订单创建核心业务，线程：{}", Thread.currentThread().getName());
    if (ThreadLocalRandom.current().nextInt(99) < 50) {
      throw new RuntimeException("业务处理异常");
    }
  }
}
