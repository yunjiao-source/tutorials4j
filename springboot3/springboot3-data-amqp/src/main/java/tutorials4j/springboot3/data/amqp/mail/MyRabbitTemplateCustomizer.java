package tutorials4j.springboot3.data.amqp.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.stereotype.Component;

/**
 * 自定义
 *
 * @author yangyunjiao
 */
@Slf4j
@Component
public class MyRabbitTemplateCustomizer implements RabbitTemplateCustomizer {
  @Override
  public void customize(RabbitTemplate rabbitTemplate) {
    log.info("RabbitTemplate 自定义配置：{}", rabbitTemplate);
    // 消息是否成功发送到Exchange
    rabbitTemplate.setConfirmCallback(
        (correlationData, ack, cause) -> {
          if (ack) {
            log.info("消息成功发送到Exchange");
          } else {
            log.info("消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
          }
        });

    // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
    rabbitTemplate.setMandatory(true);
    // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
    rabbitTemplate.setReturnsCallback(
        (returnedMessage) -> {
          log.info(
              "消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}",
              returnedMessage.getExchange(),
              returnedMessage.getRoutingKey(),
              returnedMessage.getReplyCode(),
              returnedMessage.getReplyText(),
              returnedMessage.getMessage());
        });
  }
}
