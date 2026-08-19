package tutorials4j.framework.common.spring.core;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.spring.util.EnvPropertyFinder;

/**
 * 雪花算法 ID 提供器。
 *
 * <p>从 Spring 环境配置中读取雪花算法所需的工作节点 ID（workerId）与数据中心 ID（datacenterId）， 读取失败时回退到默认值 1。
 *
 * @author Yun Jiao
 */
@Slf4j
public class SnowflakeIdProvider {

  /** 全局单例实例。 */
  public static final SnowflakeIdProvider instance = new SnowflakeIdProvider();

  private Long workerId;
  private Long datacenterId;

  private SnowflakeIdProvider() {}

  /**
   * 提供雪花算法的工作节点 ID。
   *
   * <p>首次调用时从环境配置中读取，后续复用缓存值。
   *
   * @return 工作节点 ID
   */
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

  /**
   * 提供雪花算法的数据中心 ID。
   *
   * <p>首次调用时从环境配置中读取，后续复用缓存值。
   *
   * @return 数据中心 ID
   */
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

  /**
   * 从 Spring 环境配置中读取属性值，读取失败时返回默认值 1。
   *
   * @param key 配置项 key
   * @param id 已缓存的属性值，非 null 时直接返回
   * @return 属性值
   */
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
