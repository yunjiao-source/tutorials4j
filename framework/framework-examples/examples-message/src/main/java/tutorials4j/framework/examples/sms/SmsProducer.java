package tutorials4j.framework.examples.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsProducer {

  private final StreamBridge streamBridge;

  public void sendSms(String phoneNumber, String content) {
    // 构建你的 SMS 消息对象
    SmsMessage sms = new SmsMessage(phoneNumber, content);
    // 发送消息到名为 "sms-out-0" 的通道
    streamBridge.send("sms-out-0", sms);
    log.info("SMS 消息已发送: {}", sms);
  }
}
