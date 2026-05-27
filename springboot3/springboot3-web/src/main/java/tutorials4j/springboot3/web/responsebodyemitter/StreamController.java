package tutorials4j.springboot3.web.responsebodyemitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * 基础流式推送控制器
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
public class StreamController {
  @GetMapping(value = "/stream/logs")
  public ResponseBodyEmitter streamLogs() {
    ResponseBodyEmitter emitter = new ResponseBodyEmitter();

    // 添加连接成功事件
    try {
      emitter.send("连接建立成功\n");
    } catch (IOException e) {
      emitter.completeWithError(e);
      return emitter;
    }

    // 添加连接关闭回调
    emitter.onCompletion(
        () -> {
          log.info("流式连接关闭");
        });

    emitter.onTimeout(
        () -> {
          log.warn("流式连接超时");
          emitter.complete();
        });

    // 启动异步日志推送
    startLogStreaming(emitter);

    return emitter;
  }

  public void startLogStreaming(ResponseBodyEmitter emitter) {
    // 模拟日志数据推送
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            String logLine =
                String.format(
                    "[%s] %s\n", LocalDateTime.now(), "这是实时日志内容 " + System.currentTimeMillis());

            emitter.send(logLine);
          } catch (IOException e) {
            log.error("日志推送失败", e);
            emitter.completeWithError(e);
            scheduler.shutdown();
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }
}
