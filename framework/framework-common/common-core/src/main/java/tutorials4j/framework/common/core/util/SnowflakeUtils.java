package tutorials4j.framework.common.core.util;

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
    public static final String PRO_WORKER_ID = "TUTORIALS4J_SNOWFLAKE_WORKER_ID";
    public static final String PRO_DATACENTER_ID = "TUTORIALS4J_SNOWFLAKE_DATACENTER_ID";

    private Snowflake snowflake;

    private static final SnowflakeUtils INSTANCE = new SnowflakeUtils();
    private SnowflakeUtils() {
        initSnowflake();
    }

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
        log.debug("Tutorials4j - Common |- 雪花算法ID工具初始化完成：datacenter = {}, worker = {}", datacenterId, workerId);
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
