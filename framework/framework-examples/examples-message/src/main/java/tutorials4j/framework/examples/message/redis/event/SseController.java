package tutorials4j.framework.examples.message.redis.event;

import java.io.IOException;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
public class SseController {
  private final SseEmitterManager manager;

  public SseController(SseEmitterManager manager) {
    this.manager = manager;
  }

  @GetMapping("/sse/connect")
  public SseEmitter connect(@RequestParam(required = false) String clientId) {
    // 如果未传 clientId，则生成一个随机 ID，这里也可以结合用户认证获取真实用户ID
    String id = (clientId != null) ? clientId : UUID.randomUUID().toString();
    // 超时时间设置为 30 秒（可根据需求调整），若设为 0 则永不超时
    SseEmitter emitter = new SseEmitter(60000L);
    manager.addEmitter(id, emitter);
    // 可发送一个连接成功的事件
    try {
      emitter.send(SseEmitter.event().name("connected").data("SSE 连接建立成功，clientId=" + id));
    } catch (IOException e) {
      emitter.completeWithError(e);
    }
    return emitter;
  }
}
