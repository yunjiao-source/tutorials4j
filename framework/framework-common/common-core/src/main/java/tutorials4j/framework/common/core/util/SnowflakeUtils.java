package tutorials4j.framework.common.core.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 雪花算法ID生成工具类（基于Hutool实现）。
 * <p>
 * 该工具类采用单例模式，通过系统属性 {@link #PRO_WORKER_ID} 和 {@link #PRO_DATACENTER_ID}
 * 分别配置 workerId 和 datacenterId，若未配置则默认使用 1。
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * long id = SnowflakeUtils.nextId();
 * String idStr = SnowflakeUtils.nextIdStr();
 * </pre>
 * </p>
 *
 * @author Yun Jiao
 * @see cn.hutool.core.lang.Snowflake
 */
@Slf4j
public class SnowflakeUtils {
    /**
     * 系统属性名：Worker ID（工作机器ID）。
     * 可通过 JVM 启动参数设置：-DTUTORIALS4J_SNOWFLAKE_WORKER_ID=xxx
     */
    public static final String PRO_WORKER_ID = "TUTORIALS4J_SNOWFLAKE_WORKER_ID";

    /**
     * 系统属性名：Datacenter ID（数据中心ID）。
     * 可通过 JVM 启动参数设置：-DTUTORIALS4J_SNOWFLAKE_DATACENTER_ID=xxx
     */
    public static final String PRO_DATACENTER_ID = "TUTORIALS4J_SNOWFLAKE_DATACENTER_ID";

    private Snowflake snowflake;

    private static final SnowflakeUtils INSTANCE = new SnowflakeUtils();
    private SnowflakeUtils() {
        initSnowflake();
    }

    /**
     * 初始化雪花算法引擎（线程安全）。
     * <p>该方法只会执行一次，后续调用直接返回。</p>
     *
     * @throws IllegalArgumentException 当系统属性值不是合法数字时抛出
     */
    private synchronized void initSnowflake() {
        if (snowflake != null) {
            return;
        }

        long datacenterId = 1;
        String datacenterIdStr = System.getProperty(PRO_DATACENTER_ID);
        if (StringUtils.isNotBlank(datacenterIdStr)) {
            try {
                datacenterId = Long.parseLong(datacenterIdStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Environment variable " + PRO_DATACENTER_ID + " must be number");
            }
        }

        long workerId = 1;
        String workerIdStr = System.getProperty(PRO_WORKER_ID);
        if (StringUtils.isNotBlank(workerIdStr)) {
            try {
                workerId = Long.parseLong(workerIdStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Environment variable " + PRO_WORKER_ID + " must be number");
            }
        }

        snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Common |- 雪花算法ID工具初始化完成：datacenter = {}, worker = {}", datacenterId, workerId);
        }
    }

    protected long nextId_() {
        return snowflake.nextId();
    }

    protected String nextIdStr_() {
        return snowflake.nextIdStr();
    }

    public static long nextId() {
        return INSTANCE.nextId_();
    }

    public static String nextIdStr() {
        return INSTANCE.nextIdStr_();
    }
}
