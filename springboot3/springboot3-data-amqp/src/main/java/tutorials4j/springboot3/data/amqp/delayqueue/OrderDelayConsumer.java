package tutorials4j.springboot3.data.amqp.delayqueue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费延迟消息，执行订单取消逻辑
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class OrderDelayConsumer {
  private final OrderMapper orderMapper;

  // 监听消费队列，接收到期的延迟消息
  // 配置重试机制：maxAttempts=3（重试3次），backOffInitialInterval=1000（每次重试间隔1秒）
  @RabbitListener(
      queues = RabbitMQDelayConfig.CONSUME_QUEUE,
      containerFactory = "rabbitListenerContainerFactory")
  public void consumeDelayMessage(String orderId) {
    try {
      // 1. 幂等校验：查询订单是否已取消
      Order order = orderMapper.selectByOrderId(orderId);
      if (order == null) {
        System.out.println("订单不存在，忽略消息，订单ID：" + orderId);
        return;
      }
      if (order.getIsCanceled() == 1) {
        System.out.println("订单已取消，忽略消息，订单ID：" + orderId);
        return;
      }
      // 2. 判断订单是否已支付
      if (order.getPayStatus() == 0) {
        // 未支付，执行取消逻辑
        order.setIsCanceled(1);
        order.setCancelTime(System.currentTimeMillis());
        orderMapper.updateById(order);
        System.out.println("订单30分钟未支付，已自动取消，订单ID：" + orderId);
      } else {
        // 已支付，忽略消息
        System.out.println("订单已支付，忽略消息，订单ID：" + orderId);
      }
    } catch (Exception e) {
      // 处理异常（比如数据库连接超时），抛出异常后会触发重试
      System.out.println("处理延迟消息失败，订单ID：" + orderId + "，异常信息：" + e.getMessage());
      throw e; // 必须抛出异常，否则Spring AMQP会认为消费成功，不触发重试
    }
  }
}
