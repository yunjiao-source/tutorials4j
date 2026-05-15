package tutorials4j.springboot3;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

/**
 * 邮件服务
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
public class EmailController {
  private final EmailService emailService;

  @PostMapping("/sendTemplateEmail")
  public String sendTemplateEmail(
      @RequestParam("to") String to, @RequestParam("subject") String subject)
      throws MessagingException {
    Context context = new Context();
    context.setVariable("name", "John Doe");
    emailService.sendTemplateEmail(to, subject, "email-template", context);
    return "Template Email sent successfully!";
  }
}
