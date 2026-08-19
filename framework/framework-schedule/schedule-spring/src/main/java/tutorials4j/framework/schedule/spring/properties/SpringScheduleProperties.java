package tutorials4j.framework.schedule.spring.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * Spring 调度任务配置属性。
 *
 * <p>通过 {@code spring.schedule.*} 前缀（{@link PropertiesConsts#PROPERTY_PREFIX_SCHEDULE_SPRING}） 绑定
 * Spring 调度框架的全局开关、默认执行选项以及各任务的执行配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_SPRING)
public class SpringScheduleProperties {
  /** 是否启用 Spring 调度框架。 */
  private boolean enabled = false;

  /** 是否在应用启动时自动启动所有已注册的任务。 */
  private boolean allTaskAutoStartOnBoot = true;

  /** 未单独配置时的默认任务执行选项。 */
  @NestedConfigurationProperty
  private TaskExecutionOptions defaultExecution = new TaskExecutionOptions();

  /** 按任务名称（类的简单名）配置的任务选项映射。 */
  private Map<String, TaskOptions> tasks = new HashMap<>();
}
