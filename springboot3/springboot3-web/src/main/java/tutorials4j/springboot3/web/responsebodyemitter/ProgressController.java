package tutorials4j.springboot3.web.responsebodyemitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * 进度条实时更新
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProgressController {
  private final ProgressService progressService;

  @GetMapping(value = "/stream/progress/{taskId}")
  public ResponseBodyEmitter streamProgress(@PathVariable("taskId") String taskId) {
    ResponseBodyEmitter emitter = new ResponseBodyEmitter();
    emitter.onCompletion(
        () -> {
          log.info("流式连接关闭");
        });

    emitter.onTimeout(
        () -> {
          log.warn("流式连接超时");
          emitter.complete();
        });

    // emitter.setTimeout(Long.MAX_VALUE);

    // 发送初始进度
    try {
      emitter.send("开始任务\n");
    } catch (IOException e) {
      emitter.completeWithError(e);
      return emitter;
    }

    // 启动进度监控
    startMonitor(taskId, emitter);

    return emitter;
  }

  public void startMonitor(String taskId, ResponseBodyEmitter emitter) {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(
        () -> {
          progressService.startMonitoring(
              taskId,
              progress -> {
                try {
                  String progressMessage =
                      String.format(
                          "进度: %d%% - %s\n", progress.percentage(), progress.description());
                  emitter.send(progressMessage);

                  if (progress.completed()) {
                    emitter.complete();
                  }
                } catch (IOException e) {
                  log.error("进度推送失败", e);
                  emitter.completeWithError(e);
                }
              });
        },
        0,
        1,
        TimeUnit.SECONDS);
  }
}
