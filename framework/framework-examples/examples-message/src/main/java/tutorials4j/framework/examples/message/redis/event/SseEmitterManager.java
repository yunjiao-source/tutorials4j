package tutorials4j.framework.examples.message.redis.event;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class SseEmitterManager {
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  // 添加新的连接
  public void addEmitter(String clientId, SseEmitter emitter) {
    emitters.put(clientId, emitter);
    // 当连接完成或超时，自动移除
    emitter.onCompletion(() -> emitters.remove(clientId));
    emitter.onTimeout(() -> emitters.remove(clientId));
    // 发生错误时也移除
    emitter.onError(e -> emitters.remove(clientId));
  }

  // 向所有客户端广播事件数据
  public void broadcast(Object data) {
    emitters.forEach(
        (id, emitter) -> {
          try {
            emitter.send(SseEmitter.event().data(data));
          } catch (IOException e) {
            // 发送失败则移除该 emitter
            emitters.remove(id);
          }
        });
  }

  // 定向推送给某个 clientId
  public void sendToClient(String clientId, Object data) {
    SseEmitter emitter = emitters.get(clientId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().data(data));
      } catch (IOException e) {
        emitters.remove(clientId);
      }
    }
  }
}
