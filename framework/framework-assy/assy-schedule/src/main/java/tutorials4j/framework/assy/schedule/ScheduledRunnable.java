package tutorials4j.framework.assy.schedule;

import java.time.Duration;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import tutorials4j.framework.assy.schedule.TaskEvent.TaskEventType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ScheduledRunnable implements Runnable {
  private final String taskId;
  private final Runnable actualTask;
  private final ApplicationEventPublisher eventPublisher;
  private final TaskStatisticsManager statsManager;

  public ScheduledRunnable(
      String taskId,
      Runnable actualTask,
      ApplicationEventPublisher eventPublisher,
      TaskStatisticsManager statsManager) {
    this.taskId = taskId;
    this.actualTask = actualTask;
    this.eventPublisher = eventPublisher;
    this.statsManager = statsManager;
  }

  @Override
  public void run() {
    Instant start = Instant.now();
    statsManager.updateStartTime(taskId, start);
    eventPublisher.publishEvent(new TaskEvent(this, taskId, null, TaskEventType.STARTED, null));

    try {
      actualTask.run();
      Instant end = Instant.now();
      long duration = Duration.between(start, end).toMillis();
      statsManager.recordSuccess(taskId, end, duration);
      eventPublisher.publishEvent(new TaskEvent(this, taskId, null, TaskEventType.COMPLETED, null));
    } catch (Exception e) {
      Instant end = Instant.now();
      long duration = Duration.between(start, end).toMillis();
      statsManager.recordFailure(taskId, end, duration);
      eventPublisher.publishEvent(new TaskEvent(this, taskId, null, TaskEventType.FAILED, e));
    }
  }
}
