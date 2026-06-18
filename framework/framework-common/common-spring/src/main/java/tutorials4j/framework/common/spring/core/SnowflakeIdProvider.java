package tutorials4j.framework.common.spring.core;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.EnvPropertyFinder;

/**
 * 雪花算法ID提供器
 *
 * @author Yun Jiao
 */
@Slf4j
public class SnowflakeIdProvider {

  public static final SnowflakeIdProvider instance = new SnowflakeIdProvider();

  private Long workerId;
  private Long datacenterId;

  private SnowflakeIdProvider() {}

  public long provideWorkerId() {
    if (workerId == null) {
      workerId = fetchProperty(DefaultConsts.WORKER_ID, workerId);
      if (workerId == 1L) {
        log.trace(
            "[COMMON-SPRING] Snowflake algorithm parameters use DEFAULT configuration, worker = {}",
            workerId);
      } else {
        log.trace(
            "[COMMON-SPRING] Snowflake algorithm parameters use CUSTOM configuration, workerId = {}",
            workerId);
      }
    }
    return workerId;
  }

  public long provideDataCenterId() {
    if (datacenterId == null) {
      datacenterId = fetchProperty(DefaultConsts.DATACENTER_ID, datacenterId);
      if (datacenterId == 1L) {
        log.trace(
            "[COMMON-SPRING] Snowflake algorithm parameters use DEFAULT configuration, datacenter = {}",
            datacenterId);
      } else {
        log.trace(
            "[COMMON-SPRING] Snowflake algorithm parameters use CUSTOM configuration datacenterId = {}",
            datacenterId);
      }
    }
    return datacenterId;
  }

  private synchronized Long fetchProperty(String key, Long id) {
    if (id != null) {
      return id;
    }

    Long defaultValue = 1L;
    try {
      Environment env = SpringUtil.getBean(Environment.class);
      return EnvPropertyFinder.getProperty(env, key, Long.class, defaultValue);
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
