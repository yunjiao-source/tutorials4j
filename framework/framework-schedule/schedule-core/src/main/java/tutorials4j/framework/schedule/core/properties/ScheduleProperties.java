package tutorials4j.framework.schedule.core.properties;

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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE)
public class ScheduleProperties {
  private boolean allTaskAutoStartOnBoot = true;
  private EventConsumerType eventConsumerType = EventConsumerType.sync;

  @NestedConfigurationProperty
  private TaskExecutionOptions defaultExecution = new TaskExecutionOptions();

  private Map<String, TaskOptions> tasks = new HashMap<>();

  public enum EventConsumerType {
    sync,
    async,
    custom
  }
}
