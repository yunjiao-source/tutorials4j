package tutorials4j.springboot3.web.websocket.stomp;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * 消息处理器
 *
 * @author yangyunjiao
 */
@Controller
public class GreetingController {

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public String greeting(String message) throws Exception {
    return "Hello, " + message + "!";
  }
}
