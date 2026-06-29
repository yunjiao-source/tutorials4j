package tutorials4j.framework.schedule.spring.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_SPRING)
public class SpringScheduleProperties {
  private boolean enabled = false;
  private boolean allTaskAutoStartOnBoot = true;

  @NestedConfigurationProperty
  private TaskExecutionOptions defaultExecution = new TaskExecutionOptions();

  private Map<String, TaskOptions> tasks = new HashMap<>();
}
