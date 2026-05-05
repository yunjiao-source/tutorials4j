package tutorials4j.framework.common.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 默认常量定义
 *
 * @author Yun Jiao
 */
public interface DefaultConsts {
    /**
     * 时间日期格式
     */
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认租户代码
     */
    String DEFAULT_TENTANT_CODE = "DEFAULT";

    /**
     * 默认字符集
     */
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    String MDC_CONTEXT_KEY  = "mdcSnapshot";

    String[] SESSION_IDS = new String[]{"JSESSIONID, SESSION"};

    String BEARER_TYPE = "Bearer";
    String BEARER_TOKEN = BEARER_TYPE + SymbolConsts.SPACE;

    /**
     * http header 名称定义
     */
    String HTTP_HEADER_TENANT = "X-Tenant-Code";
    String HTTP_HEADER_INNER_CALL = "X-Inner-Call";
    String HTTP_HEADER_SESSION_ID = "X-Session-Id";
    String HTTP_HEADER_OPEN_ID = "X-Open-Id";
    String HTTP_TRACE_ID = "X-Trace-Id";
    String HTTP_TRACE_SPAN_ID = "X-Trace-Span-Id";
    String HTTP_TRACE_PARENT_SPAN_ID = "X-Trace-Parent-Span-Id";
    String[] HTTP_MDC_KEYS = new String[]{DefaultConsts.HTTP_TRACE_ID,DefaultConsts.HTTP_TRACE_SPAN_ID,DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID};



    /**
     * Class 名称定义
     */
    String CLASS_HIKARI_DATA_SOURCE = "com.zaxxer.hikari.HikariDataSource";
    String CLASS_DBCP2_BASIC_DATA_SOURCE = "org.apache.commons.dbcp2.BasicDataSource";
    String CLASS_DRUID_DATA_SOURCE = "com.alibaba.druid.pool.DruidDataSource";
    String CLASS_DRUID_DATA_SOURCE_WRAPPER = "com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceWrapper";
    String CLASS_CAFFEINE_CACHE_MANAGER_CREATOR = "tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator";
    String CLASS_MULTI_LEVEL_CACHE_MANAGER_CREATOR = "tutorials4j.framework.cache.multi.MultiLevelCacheManagerCreator";
    String CLASS_REDIS_CACHE_MANAGER_CREATOR = "tutorials4j.framework.cache.redis.RedisCacheManagerCreator";
    String CLASS_TENANT_CAFFEINE_CACHE_MANAGER_CREATOR = "tutorials4j.framework.tenant.cache.TenantCaffeineCacheManagerCreator";
    String CLASS_TENANT_MULTI_LEVEL_CACHE_MANAGER_CREATOR = "tutorials4j.framework.tenant.cache.TenantMultiLevelCacheManagerCreator";


}
