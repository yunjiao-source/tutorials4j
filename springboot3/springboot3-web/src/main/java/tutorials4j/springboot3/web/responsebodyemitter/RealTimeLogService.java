package tutorials4j.springboot3.web.responsebodyemitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * 实时日志推送服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class RealTimeLogService {
  // 存储所有活跃的流式连接
  private final Map<String, ResponseBodyEmitter> activeEmitters = new ConcurrentHashMap<>();

  public ResponseBodyEmitter createLogStream(String userId, String logType) {
    String streamId = userId + ":" + logType;
    ResponseBodyEmitter emitter = new ResponseBodyEmitter();

    // 设置超时时间
    // emitter.setTimeout(Long.MAX_VALUE);

    // 存储连接
    activeEmitters.put(streamId, emitter);

    // 连接建立成功
    try {
      emitter.send("实时日志流建立成功\n");
    } catch (IOException e) {
      emitter.completeWithError(e);
    }

    // 连接关闭时清理
    emitter.onCompletion(
        () -> {
          activeEmitters.remove(streamId);
          log.info("日志流连接关闭: {}", streamId);
        });

    emitter.onTimeout(emitter::complete);

    return emitter;
  }

  public void pushLogToUser(String userId, String logType, String logMessage) {
    String streamId = userId + ":" + logType;
    ResponseBodyEmitter emitter = activeEmitters.get(streamId);

    if (emitter != null) {
      try {
        emitter.send(logMessage + "\n");
      } catch (IOException e) {
        // 发送失败，移除连接
        activeEmitters.remove(streamId);
        log.error("日志推送失败", e);
      }
    }
  }

  public void pushLogToAll(String logMessage) {
    activeEmitters.forEach(
        (streamId, emitter) -> {
          try {
            emitter.send(logMessage + "\n");
          } catch (IOException e) {
            activeEmitters.remove(streamId);
          }
        });
  }
}
