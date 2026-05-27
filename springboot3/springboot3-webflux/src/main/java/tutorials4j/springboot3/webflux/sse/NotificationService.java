package tutorials4j.springboot3.webflux.sse;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 运行
 *
 * @author yangyunjiao
 */
@Component
@RequiredArgsConstructor
public class NotificationService {
  // 用一个线程安全的Map来管理用户的连接
  private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter subscribe(String userId) {
    // 超时时间设为1小时，0表示永不过期
    SseEmitter emitter = new SseEmitter(3600_000L);
    emitters.put(userId, emitter);

    // 当连接完成或超时时，自动移除
    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));

    return emitter;
  }

  // 模拟在其他地方给特定用户发送通知
  public void sendToUser(String userId, String message) {
    SseEmitter emitter = emitters.get(userId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name("private-notification").data(message));
      } catch (IOException e) {
        emitters.remove(userId); // 发送失败，移除连接
      }
    }
  }

  @Scheduled(fixedDelay = 5000L, initialDelay = 2000L)
  public void demoSend() {
    sendToUser("user123", "message:" + new Date());
  }
}
