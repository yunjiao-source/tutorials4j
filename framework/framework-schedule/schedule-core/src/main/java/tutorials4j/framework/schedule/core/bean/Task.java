package tutorials4j.framework.schedule.core.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.util.Assert;

/**
 * 任务定义接口。
 *
 * <p>描述一个可被调度执行的任务所需的基本元信息，包括任务编码、执行类、Cron 表达式、启用状态、 描述、附加元数据、初始延迟、最大失败次数、最大执行次数以及截止时间等。实现类可通过该接口
 * 暴露任务的配置信息，供调度框架统一管理。
 *
 * @author Yun Jiao
 */
public interface Task {
  void setTaskCode(String taskCode);

  String getTaskCode();

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
    Assert.hasText(getTaskCode(), "name must not be null or empty");
    Assert.hasText(getCron(), "cron must not be null or empty");
    Assert.hasText(getClassSimpleName(), "classSimpleName must not be null or empty");
  }
}
