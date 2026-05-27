package tutorials4j.springboot3.data.amqp.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制层类
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {
  private final ProduceService testService;

  @PostMapping("send")
  public boolean sendMail(@RequestBody Mail mail) throws JsonProcessingException {
    return testService.send(mail);
  }
}
