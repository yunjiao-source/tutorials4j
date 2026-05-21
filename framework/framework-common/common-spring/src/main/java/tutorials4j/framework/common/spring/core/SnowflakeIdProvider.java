package tutorials4j.framework.common.spring.core;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.EnvPropertyFinder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class SnowflakeIdProvider {
  private static volatile SnowflakeIdProvider instance;

  private volatile Long workerId;
  private volatile Long datacenterId;
  private volatile boolean workerIdInitialized;
  private volatile boolean datacenterIdInitialized;

  private SnowflakeIdProvider() {
    // 私有构造器
  }

  public static SnowflakeIdProvider getInstance() {
    if (instance == null) {
      synchronized (SnowflakeIdProvider.class) {
        if (instance == null) {
          instance = new SnowflakeIdProvider();
        }
      }
    }
    return instance;
  }

  public long provideWorkerId() {
    if (!workerIdInitialized) {
      synchronized (this) {
        if (!workerIdInitialized) {
          workerId = fetchProperty(DefaultConsts.WORKER_ID, 1L);
          workerIdInitialized = true;
          if (workerId == 1L) {
            log.debug("[COMMON-SPRING] 框架雪花算法配置使用默认参数, worker = {}", workerId);
          } else {
            log.info("[COMMON-SPRING] 加载雪花算法 workerId = {}", workerId);
          }
        }
      }
    }
    return workerId;
  }

  public long provideDataCenterId() {
    if (!datacenterIdInitialized) {
      synchronized (this) {
        if (!datacenterIdInitialized) {
          datacenterId = fetchProperty(DefaultConsts.DATACENTER_ID, 1L);
          datacenterIdInitialized = true;
          if (datacenterId == 1L) {
            log.debug("[COMMON-SPRING] 框架雪花算法配置使用默认参数, datacenter = {}", datacenterId);
          } else {
            log.info("[COMMON-SPRING] 加载雪花算法 datacenterId = {}", datacenterId);
          }
        }
      }
    }
    return datacenterId;
  }

  private Long fetchProperty(String key, Long defaultValue) {
    try {
      Environment env = SpringUtil.getBean(Environment.class);
      return EnvPropertyFinder.getProperty(env, key, Long.class, defaultValue);
    } catch (Exception e) {
      log.error("[COMMON-SPRING] 获取环境配置失败，key={}, 将使用默认值={}", key, defaultValue, e);
      return defaultValue;
    }
  }
}
