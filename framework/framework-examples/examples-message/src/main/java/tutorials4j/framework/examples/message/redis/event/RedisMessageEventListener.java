package tutorials4j.framework.examples.message.redis.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class RedisMessageEventListener {
  private final SseEmitterManager sseEmitterManager;

  // 监听订单创建事件，推送到 SSE
  @EventListener
  public void handleOrderCreated(RedisMessageEvent event) {
    // 广播给所有连接的客户端
    sseEmitterManager.broadcast(event.message());
  }
}
