package tutorials4j.framework.schedule.core.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface Task {
  void setName(String name);

  String getName();

  void setClassSimpleName(String classSimpleName);

  String getClassSimpleName();

  void setCron(String cron);

  String getCron();

  void setEnabled(boolean enabled);

  boolean isEnabled();

  void setDescription(String description);

  String getDescription();

  void setMetadata(Map<String, String> metadata);

  Map<String, String> getMetadata();

  void setInitialDelay(Duration initialDelay);

  Duration getInitialDelay();

  void setMaxFailureCount(Integer maxFailureCount);

  Integer getMaxFailureCount();

  void setMaxExecutionCount(Integer maxExecutionCount);

  Integer getMaxExecutionCount();

  void setDueDate(Instant dueDate);

  Instant getDueDate();

  default void assertValid() {
    Assert.hasText(getName(), "name must not be null or empty");
    Assert.hasText(getCron(), "cron must not be null or empty");
    Assert.hasText(getClassSimpleName(), "classSimpleName must not be null or empty");
  }
}
