package tutorials4j.springboot3.data.amqp.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mail 邮件实体类
 *
 * @author yangyunjiao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
  private String to;

  private String title;

  private String content;

  private String msgId; // 消息id
}
