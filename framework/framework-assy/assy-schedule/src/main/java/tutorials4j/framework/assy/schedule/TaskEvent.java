package tutorials4j.framework.assy.schedule;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public class TaskEvent extends ApplicationEvent {
  private final String taskId;
  private final String taskName;
  private final TaskEventType type;
  private final Throwable error;

  public TaskEvent(
      Object source, String taskId, String taskName, TaskEventType type, Throwable error) {
    super(source);
    this.taskId = taskId;
    this.taskName = taskName;
    this.type = type;
    this.error = error;
  }

  public enum TaskEventType {
    CREATED,
    PAUSED,
    RESUMED,
    STARTED,
    COMPLETED,
    FAILED,
    DELETED
  }
}
