package tutorials4j.springboot3.data.amqp.delayqueue;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 投递逻辑
 *
 * @author Yun Jiao
 */
@Service
public class OrderService {
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private OrderMapper orderMapper; // 订单DAO层，用于操作数据库

  // 订单创建方法
  public void createOrder(OrderDTO orderDTO) {
    // 1. 保存订单到数据库（状态：未支付、未取消）
    Order order = new Order();
    order.setData(orderDTO.data());
    order.setCreateTime(System.currentTimeMillis()); // 当前时间（毫秒）
    order.setPayStatus(0); // 0=未支付，1=已支付
    order.setIsCanceled(0); // 0=未取消，1=已取消
    orderMapper.insert(order); // 插入数据库
    // 2. 投递延迟消息（30分钟后到期）
    // 消息体：直接传订单ID，简单高效，也可以传JSON字符串（包含更多信息）
    Message message =
        MessageBuilder.withBody(order.getOrderId().getBytes())
            .setExpiration(String.valueOf(Consts.expireTime)) // 单独设置每条消息的TTL
            .setDeliveryMode(MessageDeliveryMode.PERSISTENT) // 消息持久化
            .build();

    rabbitTemplate.convertAndSend(
        RabbitMQDelayConfig.DELAY_EXCHANGE, // 延迟交换机
        RabbitMQDelayConfig.DELAY_ROUTING_KEY, // 绑定键
        message);
    System.out.println("订单创建成功，延迟消息已投递，订单ID：" + order.getOrderId());
  }
}
