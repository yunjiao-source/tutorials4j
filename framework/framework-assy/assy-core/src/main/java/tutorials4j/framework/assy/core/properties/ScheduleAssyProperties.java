package tutorials4j.framework.assy.core.properties;

import java.util.HashSet;
import java.util.Set;
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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_ASSY_SCHEDULE)
public class ScheduleAssyProperties {
  @NestedConfigurationProperty private Set<TaskOptions> tasks = new HashSet<>();
}
