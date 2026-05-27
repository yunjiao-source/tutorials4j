package tutorials4j.springboot3.data.amqp.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者类
 *
 * @author yangyunjiao
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumerMailService {
  private final SendMailUtil sendMailUtil;
  private final ObjectMapper objectMapper;

  @RabbitListener(queues = RabbitConfig.MAIL_QUEUE_NAME)
  public void consume(Message message, Channel channel) throws IOException {
    // 将消息转化为对象
    String str = new String(message.getBody());
    Mail mail = objectMapper.readValue(str, Mail.class);
    log.info("收到消息: {}", mail.toString());

    MessageProperties properties = message.getMessageProperties();
    long tag = properties.getDeliveryTag();

    boolean success = sendMailUtil.send(mail);
    if (success) {
      channel.basicAck(tag, false); // 消费确认
    } else {
      channel.basicNack(tag, false, true);
    }
  }
}
