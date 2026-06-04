package tutorials4j.springboot3.data.amqp.orderedmessage;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

// 消息实体类（封装消息相关信息）
@Data
public class OrderMessage implements Serializable {
  private Long orderId;
  private String messageType;
  private String content;
  private Date sendTime;

  public OrderMessage() {}

  public OrderMessage(Long orderId, String messageType, String content, Date sendTime) {
    this.orderId = orderId;
    this.messageType = messageType;
    this.content = content;
    this.sendTime = sendTime;
  }

  public static OrderMessage of(Long orderId, String messageType, String content) {
    return new OrderMessage(orderId, messageType, content, new Date());
  }
}
