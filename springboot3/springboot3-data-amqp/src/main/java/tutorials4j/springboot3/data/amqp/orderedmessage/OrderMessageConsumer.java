package tutorials4j.springboot3.data.amqp.orderedmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费逻辑（核心：单线程消费）
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class OrderMessageConsumer {
  // 用于记录每个订单的最新消费消息类型（避免乱序）
  // key：订单ID，value：最新消费的消息类型（CREATE->PAY->DELIVER）
  private final OrderRepository orderRepository;
  private final Map<Long, String> orderLastMessageType = new ConcurrentHashMap<>();

  // 消费者1：消费order_queue_1队列
  @RabbitListener(queues = RabbitConfig.ORDER_QUEUE_PREFIX + "1")
  public void consumeQueue1(Message message, Channel channel) throws IOException {
    handleMessage(message, channel);
  }

  // 消费者2：消费order_queue_2队列
  @RabbitListener(queues = RabbitConfig.ORDER_QUEUE_PREFIX + "2")
  public void consumeQueue2(Message message, Channel channel) throws IOException {
    handleMessage(message, channel);
  }

  // 消费者3：消费order_queue_3队列
  @RabbitListener(queues = RabbitConfig.ORDER_QUEUE_PREFIX + "3")
  public void consumeQueue3(Message message, Channel channel) throws IOException {
    handleMessage(message, channel);
  }

  // 统一处理消息的逻辑（核心：判断顺序、单线程消费）
  private void handleMessage(Message message, Channel channel) throws IOException {
    // 1. 解析消息体
    ObjectMapper objectMapper = new ObjectMapper();
    OrderMessage orderMessage = null;
    try {
      orderMessage = objectMapper.readValue(message.getBody(), OrderMessage.class);
    } catch (Exception e) {
      // 消息格式错误，直接拒绝，不重新入队（避免死循环）
      channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
      System.err.println("消息格式错误，拒绝消费：" + new String(message.getBody()));
      return;
    }
    Long orderId = orderMessage.getOrderId();
    String currentMessageType = orderMessage.getMessageType();
    System.out.println("收到消息：订单ID=" + orderId + "，消息类型=" + currentMessageType);
    // 2. 核心：判断消息顺序是否正确（按CREATE->PAY->DELIVER的顺序）
    String lastMessageType =
        orderLastMessageType.computeIfAbsent(orderId, orderRepository::get); // 初始状态为INIT
    if (!checkMessageOrder(lastMessageType, currentMessageType)) {
      // 顺序错误，拒绝消费，并且重新入队（等待正确顺序的消息先消费）
      channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
      System.err.println(
          "消息顺序错误：订单ID=" + orderId + "，当前消息=" + currentMessageType + "，上一条消息=" + lastMessageType);
      return;
    }
    // 3. 顺序正确，处理消息业务逻辑（这里替换成自己的业务代码）
    try {
      if ("CREATE".equals(currentMessageType)) {
        handleOrderCreate(orderId); // 处理订单创建
      } else if ("PAY".equals(currentMessageType)) {
        handleOrderPay(orderId); // 处理订单支付
      } else if ("DELIVER".equals(currentMessageType)) {
        handleOrderDeliver(orderId); // 处理订单发货
      }
      // 4. 处理成功，更新该订单的最新消息类型
      orderLastMessageType.put(orderId, currentMessageType);
      // 手动ACK，告诉MQ消息已处理完成，可删除
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
      System.out.println("消息处理成功：订单ID=" + orderId + "，消息类型=" + currentMessageType);
    } catch (Exception e) {
      // 业务处理失败，重新入队（最多重试3次，避免死循环，后续可加重试机制）
      int retryCount =
          message.getMessageProperties().getHeader("retry-count") == null
              ? 0
              : (int) message.getMessageProperties().getHeader("retry-count");
      if (retryCount < 3) {
        message.getMessageProperties().setHeader("retry-count", retryCount + 1);
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        System.err.println("消息处理失败，重新入队：订单ID=" + orderId + "，重试次数=" + (retryCount + 1));
      } else {
        // 重试3次失败，拒绝入队，记录日志，人工处理
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        System.err.println("消息处理失败，重试3次仍失败，订单ID=" + orderId);
        // 这里可以加日志记录、告警通知等逻辑
      }
    }
  }

  // 校验消息顺序：只能从INIT→CREATE→PAY→DELIVER
  private boolean checkMessageOrder(String lastType, String currentType) {
    return switch (lastType) {
      case "INIT" -> "CREATE".equals(currentType); // 初始状态，只能先消费创建消息
      case "CREATE" -> "PAY".equals(currentType); // 上一条是创建，下一条只能是支付
      case "PAY" -> "DELIVER".equals(currentType); // 上一条是支付，下一条只能是发货
      case "DELIVER" -> false; // 发货是最后一条消息，不能再消费其他消息
      default -> false;
    };
  }

  // 模拟订单创建业务
  private void handleOrderCreate(Long orderId) {
    System.out.println("处理订单创建：订单ID=" + orderId);
    orderRepository.put(orderId, "CREATE");
  }

  // 模拟订单支付业务
  private void handleOrderPay(Long orderId) {
    System.out.println("处理订单支付：订单ID=" + orderId);
    orderRepository.put(orderId, "PAY");
  }

  // 模拟订单发货业务
  private void handleOrderDeliver(Long orderId) {
    System.out.println("处理订单发货：订单ID=" + orderId);
    orderRepository.put(orderId, "DELIVER");
  }
}
