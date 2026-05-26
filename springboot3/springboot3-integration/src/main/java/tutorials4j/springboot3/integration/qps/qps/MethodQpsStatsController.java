package tutorials4j.springboot3.integration.qps.qps;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST端点来查看统计信息
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/method-qps-stats")
public class MethodQpsStatsController {

  private final MethodQpsAspect methodQpsAspect;

  public MethodQpsStatsController(MethodQpsAspect methodQpsAspect) {
    this.methodQpsAspect = methodQpsAspect;
  }

  /** 获取所有方法的统计信息 */
  @GetMapping
  public Map<String, MethodCallStats> getAllStats() {
    return methodQpsAspect.getAllRecentCallStats();
  }

  /** 获取特定方法的统计信息 */
  @GetMapping("/{methodName}")
  public MethodCallStats getMethodStats(@PathVariable String methodName) {
    return methodQpsAspect.getRecentCallStats(methodName);
  }

  /** 获取特定方法的最近调用时间详情 */
  @GetMapping("/{methodName}/details")
  public Map<String, Object> getMethodDetails(@PathVariable String methodName) {
    MethodCallStats stats = methodQpsAspect.getRecentCallStats(methodName);
    if (stats.callCount() == 0) {
      return Map.of("method", methodName, "message", "No calls recorded");
    }

    return Map.of(
        "method",
        methodName,
        "callCount",
        stats.callCount(),
        "avgTime",
        String.format("%.2f ms", stats.avgTime()),
        "minTime",
        stats.minTime() + " ms",
        "maxTime",
        stats.maxTime() + " ms",
        "totalTime",
        stats.totalTime() + " ms",
        "recentCallTimes",
        stats.recentCallTimes());
  }
}
