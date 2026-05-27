package tutorials4j.springboot3.web.responsebodyemitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * 连接池管理
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class StreamConnectionPool {
  private final Map<String, ResponseBodyEmitter> emitters = new ConcurrentHashMap<>();
  private final Map<String, ScheduledExecutorService> schedulers = new ConcurrentHashMap<>();

  public ResponseBodyEmitter createStream(String streamId) {
    ResponseBodyEmitter emitter = new ResponseBodyEmitter();
    // emitter.setTimeout(config.getTimeout());

    // 存储连接
    emitters.put(streamId, emitter);

    // 创建调度器
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    schedulers.put(streamId, scheduler);

    // 连接管理回调
    emitter.onCompletion(
        () -> {
          emitters.remove(streamId);
          ScheduledExecutorService removedScheduler = schedulers.remove(streamId);
          if (removedScheduler != null) {
            removedScheduler.shutdown();
          }
        });
    emitter.onCompletion(
        () -> {
          log.info("流式连接关闭");
        });

    emitter.onTimeout(
        () -> {
          log.warn("流式连接超时");
          emitter.complete();
        });

    return emitter;
  }

  public boolean sendToStream(String streamId, String data) {
    ResponseBodyEmitter emitter = emitters.get(streamId);
    if (emitter != null) {
      try {
        emitter.send(data + "\n");
        return true;
      } catch (IOException e) {
        emitter.complete();
        return false;
      }
    }
    return false;
  }
}
