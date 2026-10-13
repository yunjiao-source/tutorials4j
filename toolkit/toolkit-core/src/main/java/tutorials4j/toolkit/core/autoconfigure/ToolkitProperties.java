package tutorials4j.toolkit.core.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT)
public class ToolkitProperties {
  @NestedConfigurationProperty
  private ScheduledExecutionOptions scheduledThreadPoolExecutor = new ScheduledExecutionOptions();

  @NestedConfigurationProperty
  private ThreadExecutionOptions threadPoolExecutor = new ThreadExecutionOptions();
}
