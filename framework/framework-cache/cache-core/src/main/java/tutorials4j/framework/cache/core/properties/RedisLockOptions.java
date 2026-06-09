package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.ExecutionOption;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class RedisLockOptions {
  @NestedConfigurationProperty private ExecutionOption autoRenewal = new ExecutionOption();
}
