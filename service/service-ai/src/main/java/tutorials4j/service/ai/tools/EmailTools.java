package tutorials4j.service.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class EmailTools {
  @Tool(name = "find-email", description = "Finds an email address by a person's name")
  public String findEmail(String name) {
    Utils.greenSystemOut("findEmail 工具调用：" + name);
    if (name.startsWith("张")) {
      return "12345678@qq.com";
    }
    return null;
  }

  @Tool(
      name = "send-email",
      description = "Sends an email to a recipient",
      requiresConfirmation = true) // ← 关键标记
  public String sendEmail(String recipient, String subject, String body) {
    Utils.greenSystemOut("sendEmail 工具调用：" + recipient + ", " + subject + ", " + body);
    return "发送完成";
  }
}
