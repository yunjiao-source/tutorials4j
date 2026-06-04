package tutorials4j.springboot3.data.amqp.delayqueue;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库兜底机制（关键，避免消息丢失）
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class OrderCancelScheduled {
  private final OrderMapper orderMapper;

  // 定时任务
  @Scheduled(fixedDelay = 6000)
  public void cancelTimeoutOrder() {
    // 1. 查询：创建时间超过30分钟、未支付、未取消的订单
    // 30分钟 = 30 * 60 * 1000 毫秒，当前时间 - 30分钟 = 超时时间阈值
    long timeout = System.currentTimeMillis() - Consts.expireTime;
    List<Order> timeoutOrders = orderMapper.selectTimeoutOrder(timeout);
    // 2. 批量取消订单
    for (Order order : timeoutOrders) {
      order.setIsCanceled(1);
      order.setCancelTime(System.currentTimeMillis());
      orderMapper.updateById(order);
      System.out.println("兜底机制：订单超时未支付，已自动取消，订单ID：" + order.getOrderId());
    }
  }
}
