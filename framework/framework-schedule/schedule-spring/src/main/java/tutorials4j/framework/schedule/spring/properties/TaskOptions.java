package tutorials4j.framework.schedule.spring.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 单个任务的配置选项。
 *
 * <p>描述一个任务的类名、cron 表达式、启用状态、描述信息、元数据及执行选项。
 *
 * @author Yun Jiao
 */
@Data
public class TaskOptions {
  /** 任务类的简单类名。 */
  private String classSimpleName;

  /** 任务的 cron 表达式。 */
  private String cron;

  /** 是否启用该任务。 */
  private boolean enabled;

  /** 任务描述。 */
  private String description;

  /** 任务的附加元数据。 */
  private Map<String, String> metadata = new HashMap<>();

  /** 该任务的执行选项。 */
  @NestedConfigurationProperty private TaskExecutionOptions execution = new TaskExecutionOptions();

  /**
   * 校验任务配置是否有效。
   *
   * @return 类名与 cron 表达式均非空白时返回 {@code true}，否则返回 {@code false}
   */
  public boolean validate() {
    return !StringUtils.isAnyBlank(classSimpleName, cron);
  }
}
