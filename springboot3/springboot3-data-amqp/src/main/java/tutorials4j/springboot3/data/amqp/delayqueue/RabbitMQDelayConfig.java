package tutorials4j.springboot3.data.amqp.delayqueue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * RabbitMQ延迟队列
 *
 * @author Yun Jiao
 */
@EnableScheduling
@Configuration
public class RabbitMQDelayConfig {
  // 1. 延迟队列（存储延迟消息，消息到期后会转发到消费队列）
  public static final String DELAY_QUEUE = "order_1_delay_queue";
  // 2. 消费队列（接收到期的延迟消息，执行订单取消逻辑）
  public static final String CONSUME_QUEUE = "order_1_consume_queue";
  // 3. 延迟交换机（转发延迟消息到延迟队列）
  public static final String DELAY_EXCHANGE = "order_1_delay_exchange";
  // 4. 消费交换机（转发到期消息到消费队列）
  public static final String CONSUME_EXCHANGE = "order_1_consume_exchange";
  // 5. 延迟队列绑定键
  public static final String DELAY_ROUTING_KEY = "delay_1.routing.key";
  // 6. 消费队列绑定键
  public static final String CONSUME_ROUTING_KEY = "consume_1.routing.key";

  // 配置延迟队列（关键：设置TTL和死信交换机）
  @Bean
  public Queue delayQueue() {
    return QueueBuilder.durable(DELAY_QUEUE) // 队列持久化，避免MQ重启后消息丢失
        .withArgument("x-dead-letter-exchange", CONSUME_EXCHANGE) // 消息过期后，转发到消费交换机
        .withArgument("x-dead-letter-routing-key", CONSUME_ROUTING_KEY) // 转发时的绑定键
        .withArgument("x-message-ttl", 30 * 60 * 1000) // TTL：30分钟（单位：毫秒），消息30分钟后过期
        .build();
  }

  // 配置消费队列（用于接收到期消息，执行取消逻辑）
  @Bean
  public Queue consumeQueue() {
    return QueueBuilder.durable(CONSUME_QUEUE) // 队列持久化
        .build();
  }

  // 配置延迟交换机（扇形交换机，简单易上手，适合新手）
  @Bean
  public FanoutExchange delayExchange() {
    return new FanoutExchange(DELAY_EXCHANGE, true, false); // 交换机持久化
  }

  // 配置消费交换机（同样用扇形交换机）
  @Bean
  public FanoutExchange consumeExchange() {
    return new FanoutExchange(CONSUME_EXCHANGE, true, false);
  }

  // 绑定：延迟交换机 -> 延迟队列
  @Bean
  public Binding delayBinding() {
    return BindingBuilder.bind(delayQueue()).to(delayExchange());
  }

  // 绑定：消费交换机 -> 消费队列
  @Bean
  public Binding consumeBinding() {
    return BindingBuilder.bind(consumeQueue()).to(consumeExchange());
  }
}
