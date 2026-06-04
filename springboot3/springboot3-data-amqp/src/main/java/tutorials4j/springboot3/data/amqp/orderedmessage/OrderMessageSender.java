package tutorials4j.springboot3.data.amqp.orderedmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息发送逻辑（核心：路由到同一个队列）
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class OrderMessageSender {
  private final RabbitTemplate rabbitTemplate;

  // 发送订单消息（参数：订单ID、消息类型、消息内容）
  public void sendOrderMessage(OrderMessage message) {
    // 2. 核心：根据订单ID，计算要发送到哪个队列
    // 哈希取模：orderId的哈希值 % 队列数量，得到队列索引（0、1、2）
    int queueIndex = Math.abs(message.getOrderId().hashCode()) % RabbitConfig.QUEUE_COUNT;
    // 拼接队列名称：order_queue_1、order_queue_2、order_queue_3
    String queueName = RabbitConfig.ORDER_QUEUE_PREFIX + (queueIndex + 1);
    // 3. 发送消息（路由键=队列名称，确保消息路由到指定队列）
    rabbitTemplate.convertAndSend(
        RabbitConfig.ORDER_EXCHANGE,
        queueName,
        message,
        // 消息持久化，防止MQ重启丢失消息
        messagePostProcessor -> {
          MessageProperties properties = messagePostProcessor.getMessageProperties();
          properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
          return messagePostProcessor;
        });
    System.out.println(
        "消息发送成功：订单ID="
            + message.getOrderId()
            + "，队列="
            + queueName
            + "，消息类型="
            + message.getMessageType());
  }
}
