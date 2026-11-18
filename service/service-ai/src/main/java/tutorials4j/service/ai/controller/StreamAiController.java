package tutorials4j.service.ai.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("stream")
@RequiredArgsConstructor
public class StreamAiController {
  private final ChatClient defaultChatClient;

  @GetMapping(value = "/simple", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
  public Flux<String> simple(HttpServletResponse response) {
    response.setCharacterEncoding("UTF-8");
    return defaultChatClient.prompt("请简单介绍一下你自己").stream().content();
  }
}
