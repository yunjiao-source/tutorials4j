package tutorials4j.springboot3.data.amqp.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 生产者类
 *
 * @author yangyunjiao
 */
@Service
@RequiredArgsConstructor
public class ProduceService {
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public boolean send(Mail mail) throws JsonProcessingException {
    // 创建uuid
    String msgId = UUID.randomUUID().toString().replaceAll("-", "");
    mail.setMsgId(msgId);

    // 发送消息到rabbitMQ
    CorrelationData correlationData = new CorrelationData(msgId);
    rabbitTemplate.convertAndSend(
        RabbitConfig.MAIL_EXCHANGE_NAME,
        RabbitConfig.MAIL_ROUTING_KEY_NAME,
        objectMapper.writeValueAsString(mail),
        correlationData);

    return true;
  }
}
