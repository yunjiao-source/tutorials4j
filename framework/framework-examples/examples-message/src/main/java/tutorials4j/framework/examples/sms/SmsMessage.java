package tutorials4j.framework.examples.sms;

import lombok.Data;

@Data
public class SmsMessage {
  private String phoneNumber;
  private String content;

  // 构造函数、getter 和 setter 方法
  public SmsMessage() {}

  public SmsMessage(String phoneNumber, String content) {
    this.phoneNumber = phoneNumber;
    this.content = content;
  }

  // 省略 getter 和 setter...
}
