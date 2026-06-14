package tutorials4j.framework.schedule.core.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class TaskOptions {
  private String classSimpleName;
  private String cron;
  private boolean enabled;
  private String description;
  private Map<String, String> metadata = new HashMap<>();
  @NestedConfigurationProperty private TaskExecutionOptions execution = new TaskExecutionOptions();

  public boolean validate() {
    return !StringUtils.isAnyBlank(classSimpleName, cron);
  }
}
