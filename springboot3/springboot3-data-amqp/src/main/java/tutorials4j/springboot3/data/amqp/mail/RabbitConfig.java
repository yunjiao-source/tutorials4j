package tutorials4j.springboot3.data.amqp.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author yangyunjiao
 */
@Configuration
@Slf4j
public class RabbitConfig {

  // 发送邮件
  public static final String MAIL_QUEUE_NAME = "mail.queue";
  public static final String MAIL_EXCHANGE_NAME = "mail.exchange";
  public static final String MAIL_ROUTING_KEY_NAME = "mail.routing.key";

  @Bean
  public Queue mailQueue() {
    return new Queue(MAIL_QUEUE_NAME, true);
  }

  @Bean
  public DirectExchange mailExchange() {
    return new DirectExchange(MAIL_EXCHANGE_NAME, true, false);
  }

  @Bean
  public Binding mailBinding() {
    return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY_NAME);
  }
}
