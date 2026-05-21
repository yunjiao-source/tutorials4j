package tutorials4j.framework.common.spring.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.spring.core.SnowflakeIdProvider;

/**
 * 雪花算法ID生成工具类（基于Hutool实现）。
 *
 * <p>该工具类采用单例模式，通过系统属性 {@link #PRO_WORKER_ID} 和 {@link #PRO_DATACENTER_ID} 分别配置 workerId 和
 * datacenterId，若未配置则默认使用 1。
 *
 * <p>使用示例：
 *
 * <pre>
 * long id = SnowflakeUtils.nextId();
 * String idStr = SnowflakeUtils.nextIdStr();
 * </pre>
 *
 * @author Yun Jiao
 * @see cn.hutool.core.lang.Snowflake
 */
@Slf4j
public class SnowflakeUtils {
  private Snowflake snowflake;

  private static SnowflakeUtils INSTANCE;

  private SnowflakeUtils() {
    initSnowflake();
  }

  /**
   * 初始化雪花算法引擎（线程安全）。
   *
   * <p>该方法只会执行一次，后续调用直接返回。
   *
   * @throws IllegalArgumentException 当系统属性值不是合法数字时抛出
   */
  private synchronized void initSnowflake() {
    if (snowflake != null) {
      return;
    }

    long datacenterId = SnowflakeIdProvider.getInstance().provideDataCenterId();
    long workerId = SnowflakeIdProvider.getInstance().provideWorkerId();

    snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    if (log.isDebugEnabled()) {
      log.debug("[COMMON-CORE] 雪花算法ID工具初始化完成：datacenter = {}, worker = {}", datacenterId, workerId);
    }
  }

  protected static SnowflakeUtils getInstance() {
    if (ObjectUtils.isEmpty(INSTANCE)) {
      synchronized (SnowflakeUtils.class) {
        if (ObjectUtils.isEmpty(INSTANCE)) {
          INSTANCE = new SnowflakeUtils();
        }
      }
    }

    return INSTANCE;
  }

  protected long nextLongId() {
    return snowflake.nextId();
  }

  protected String nextStrId() {
    return snowflake.nextIdStr();
  }

  public static long nextId() {
    return getInstance().nextLongId();
  }

  public static String nextIdStr() {
    return getInstance().nextStrId();
  }
}
