package tutorials4j.toolkit.core.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class SnowflakeUtils {
  public static final String PRO_WORKER_ID = "TUTORIALS4J_WORKER_ID";
  public static final String PRO_DATACENTER_ID = "TUTORIALS4J_DATACENTER_ID";

  private static Snowflake snowflake;

  private SnowflakeUtils() {}

  private static synchronized void initSnowflake() {
    if (snowflake != null) {
      return;
    }

    long datacenterId = 1;
    String datacenterIdStr = System.getProperty(PRO_DATACENTER_ID);
    if (StringUtils.isNotBlank(datacenterIdStr)) {
      try {
        datacenterId = Long.parseLong(datacenterIdStr);
      } catch (NumberFormatException e) {
        log.warn("环境变量 {} 应该设置数字值，实际值 {}；系统将默认 1", PRO_DATACENTER_ID, datacenterIdStr);
      }
    }

    long workerId = 1;
    String workerIdStr = System.getProperty(PRO_WORKER_ID);
    if (StringUtils.isNotBlank(workerIdStr)) {
      try {
        workerId = Long.parseLong(workerIdStr);
      } catch (NumberFormatException e) {
        log.warn("环境变量 {} 应该设置数字值，实际值 {}；系统将默认 1", PRO_WORKER_ID, workerIdStr);
      }
    }

    snowflake = IdUtil.getSnowflake(workerId, datacenterId);

    log.info("雪花算法工具初始化完成，worker={}, datacenter={}", workerId, datacenterId);
  }

  public static long nextId() {
    if (snowflake == null) {
      initSnowflake();
    }
    return snowflake.nextId();
  }

  public static String nextIdStr() {
    if (snowflake == null) {
      initSnowflake();
    }
    return snowflake.nextIdStr();
  }
}
