package tutorials4j.springboot3.web.responsebodyemitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/stream/realtime")
@RequiredArgsConstructor
public class RealTimeLogController {

  private final RealTimeLogService realTimeLogService;

  // 建立实时日志流连接
  @GetMapping("/{userId}/{logType}")
  public ResponseBodyEmitter createLogStream(
      @PathVariable("userId") String userId, @PathVariable("logType") String logType) {
    return realTimeLogService.createLogStream(userId, logType);
  }

  // 向指定用户和日志类型推送一条消息（演示用）
  @PostMapping("/{userId}/{logType}")
  public String pushLog(
      @PathVariable("userId") String userId,
      @PathVariable("logType") String logType,
      @RequestParam("message") String message) {
    realTimeLogService.pushLogToUser(userId, logType, message);
    return "推送成功";
  }

  // 广播消息给所有连接
  @PostMapping("/broadcast")
  public String broadcast(@RequestParam("message") String message) {
    realTimeLogService.pushLogToAll(message);
    return "广播成功";
  }
}
