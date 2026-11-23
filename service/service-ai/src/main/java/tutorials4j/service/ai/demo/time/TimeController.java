package tutorials4j.service.ai.demo.time;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("time")
@RequiredArgsConstructor
public class TimeController {
  private final ChatFacade chatFacade;
  private final TimeService timeService;

  @GetMapping("/now")
  public String functionCall1(String city) {
    return chatFacade
        .client()
        .prompt()
        .tools(new TimeTools(timeService))
        .user(city + "现在几点了？")
        .call()
        .content();
  }
}
