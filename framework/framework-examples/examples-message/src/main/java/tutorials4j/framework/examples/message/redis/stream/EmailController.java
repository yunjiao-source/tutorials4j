package tutorials4j.framework.examples.message.redis.stream;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
public class EmailController {
  private final EmailService emailService;

  @GetMapping("send")
  public String send() {
    IntStream.range(0, 5).forEach((i) -> emailService.send());
    return "ok";
  }
}
