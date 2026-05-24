package tutorials4j.framework.assy.schedule;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class TaskStatisticsManager {
  private final Map<String, TaskStatistics> statsMap = new ConcurrentHashMap<>();

  public void updateStartTime(String taskId, Instant start) {
    statsMap.computeIfAbsent(taskId, id -> new TaskStatistics()).setLastStartTime(start);
  }

  public void recordSuccess(String taskId, Instant completeTime, long durationMs) {
    TaskStatistics stats = statsMap.computeIfAbsent(taskId, id -> new TaskStatistics());
    stats.setSuccessCount(stats.getSuccessCount() + 1);
    stats.setTotalDurationMs(stats.getTotalDurationMs() + durationMs);
    stats.setLastCompleteTime(completeTime);
  }

  public void recordFailure(String taskId, Instant completeTime, long durationMs) {
    TaskStatistics stats = statsMap.computeIfAbsent(taskId, id -> new TaskStatistics());
    stats.setFailureCount(stats.getFailureCount() + 1);
    stats.setTotalDurationMs(stats.getTotalDurationMs() + durationMs);
    stats.setLastCompleteTime(completeTime);
  }

  public void removeStatistics(String taskId) {
    statsMap.remove(taskId);
  }

  public TaskStatistics getStatistics(String taskId) {
    TaskStatistics stats = statsMap.get(taskId);
    if (stats == null) return new TaskStatistics();
    // 计算平均耗时
    long totalExecutions = stats.getSuccessCount() + stats.getFailureCount();
    if (totalExecutions > 0) {
      stats.setAvgDurationMs(stats.getTotalDurationMs() / totalExecutions);
    }
    // 下次执行时间需要根据当前任务定义获取（由调用方设置）
    return stats;
  }
}
