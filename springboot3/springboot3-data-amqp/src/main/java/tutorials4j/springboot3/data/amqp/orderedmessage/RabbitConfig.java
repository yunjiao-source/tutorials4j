package tutorials4j.springboot3.data.amqp.orderedmessage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 创建3个队列（可根据实际并发量调整，队列越多，并发越高），所有队列绑定到同一个交换机，通过路由键区分
 *
 * @author Yun Jiao
 */
@EnableScheduling
@Configuration
public class RabbitConfig {
  // 交换机名称（统一交换机，所有订单消息都走这里）
  public static final String ORDER_EXCHANGE = "order_2_exchange";
  // 队列前缀（创建3个队列，后缀1、2、3）
  public static final String ORDER_QUEUE_PREFIX = "order_2_queue_";
  // 队列数量（可根据并发调整，比如10个、20个）
  public static final int QUEUE_COUNT = 3;

  // 1. 配置交换机（topic类型，支持路由键匹配）
  @Bean
  public TopicExchange orderExchange() {
    // durable=true：交换机持久化，重启MQ不会丢失
    return new TopicExchange(ORDER_EXCHANGE, true, false);
  }

  // 2. 配置多个队列，并绑定到交换机
  @Bean
  public Declarables orderQueueList() {
    List<Declarable> declarables = new ArrayList<>();
    for (int i = 1; i <= QUEUE_COUNT; i++) {
      String queueName = ORDER_QUEUE_PREFIX + i;
      Queue queue = new Queue(queueName, true);
      declarables.add(queue);
      declarables.add(BindingBuilder.bind(queue).to(orderExchange()).with(queueName));
    }
    return new Declarables(declarables.toArray(new Declarable[0]));
  }

  /**
   * Caused by: java.lang.SecurityException: Attempt to deserialize unauthorized class
   * tutorials4j.springboot3.data.amqp.orderedmessage.OrderMessage; add allowed class name patterns
   * to the message converter or, if you trust the message originator, set environment variable
   * 'SPRING_AMQP_DESERIALIZATION_TRUST_ALL' or system property
   * 'spring.amqp.deserialization.trust.all' to true
   *
   * @return
   */
  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    Jackson2JavaTypeMapper typeMapper = converter.getJavaTypeMapper();
    // 配置信任的包前缀，支持通配符
    typeMapper.addTrustedPackages("tutorials4j.springboot3.data.amqp.orderedmessage");
    // 多个包继续addTrustedPackages("com.xxx","com.xxx2")
    return converter;
  }
}
