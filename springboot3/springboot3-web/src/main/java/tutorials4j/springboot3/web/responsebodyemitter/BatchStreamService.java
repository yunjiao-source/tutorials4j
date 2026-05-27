package tutorials4j.springboot3.web.responsebodyemitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * 缓存与批量推送
 *
 * @author Yun Jiao
 */
@Service
public class BatchStreamService {
  private final Map<String, List<String>> messageBuffers = new ConcurrentHashMap<>();
  private final ScheduledExecutorService batchScheduler = Executors.newScheduledThreadPool(5);

  public void addToBuffer(String streamId, String message) {
    messageBuffers.computeIfAbsent(streamId, k -> new ArrayList<>()).add(message);
  }

  public void startBatchProcessing(String streamId, int batchSize, long intervalMs) {
    batchScheduler.scheduleAtFixedRate(
        () -> {
          List<String> messages = messageBuffers.get(streamId);
          if (messages != null && !messages.isEmpty()) {
            synchronized (messages) {
              if (!messages.isEmpty()) {
                List<String> batch = new ArrayList<>();
                int size = Math.min(batchSize, messages.size());
                for (int i = 0; i < size; i++) {
                  batch.add(messages.removeFirst());
                }

                // 批量发送
                sendBatchToStream(streamId, batch);
              }
            }
          }
        },
        0,
        intervalMs,
        TimeUnit.MILLISECONDS);
  }

  private void sendBatchToStream(String streamId, List<String> messages) {
    // String batchData = JSON.toJSONString(messages);
    // 发送到对应的流
    // 实现逻辑...
  }
}
