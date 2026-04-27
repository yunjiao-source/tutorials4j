package tutorials4j.framework.common.core;

/**
 * 基础属性配置常量
 *
 * @author Yun Jiao
 */
public interface PropertiesConsts {
    String PROPERTY_ENABLED = ".enabled";
    String PROPERTY_PREFIX_SPRING = "spring";
    String PROPERTY_PREFIX_TUTORIALS4J = "tutorials4j";

    /* ---------- 自定义配置属性 ---------- */

    /** cache */
    String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_TUTORIALS4J + ".cache";
    String PROPERTY_PREFIX_CACHE_REDIS = PROPERTY_PREFIX_CACHE + ".redis";
    String PROPERTY_PREFIX_CACHE_CAFFEINE = PROPERTY_PREFIX_CACHE + ".caffeine";

    /** web */
    String PROPERTY_PREFIX_WEB = PROPERTY_PREFIX_TUTORIALS4J + ".web";
    String PROPERTY_PREFIX_WEB_HTTP = PROPERTY_PREFIX_WEB + ".http";
    String PROPERTY_PREFIX_WEB_HTTP_CACHED_REQUEST_BODY = PROPERTY_PREFIX_WEB_HTTP + ".cached-request-body";
    String PROPERTY_PREFIX_WEB_CLIENT = PROPERTY_PREFIX_WEB + ".client";

    /** data */
    String PROPERTY_PREFIX_DATA = PROPERTY_PREFIX_TUTORIALS4J + ".data";

    /** tenant */
    String PROPERTY_PREFIX_TENANT = PROPERTY_PREFIX_TUTORIALS4J + ".tenant";
    String PROPERTY_PREFIX_TENANT_DATASOURCE = PROPERTY_PREFIX_TENANT + ".datasource";
    String PROPERTY_PREFIX_TENANT_CACHE = PROPERTY_PREFIX_TENANT + ".cache";
}
