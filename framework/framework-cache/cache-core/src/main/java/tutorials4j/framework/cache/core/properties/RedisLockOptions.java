package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.ExecutionOption;

/**
 * Redis 锁配置选项。
 *
 * <p>用于配置基于 Redis（Redisson）实现的分布式锁的相关参数。
 *
 * @author Yun Jiao
 */
@Data
public class RedisLockOptions {
  /** 自动续期执行选项，用于配置看门狗（watchdog）自动续期任务的线程池参数。 */
  @NestedConfigurationProperty private ExecutionOption autoRenewal = new ExecutionOption();
}
