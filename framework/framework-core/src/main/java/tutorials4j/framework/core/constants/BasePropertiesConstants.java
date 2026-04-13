package tutorials4j.framework.core.constants;

/**
 * 基础属性配置常量
 *
 * @author Yun Jiao
 */
public interface BasePropertiesConstants {
    String PROPERTY_ENABLED = ".enabled";
    String PROPERTY_PREFIX_SPRING = "spring";
    String PROPERTY_PREFIX_TUTORIALS4J = "tutorials4j";

    /* ---------- 自定义配置属性 ---------- */
    String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_TUTORIALS4J + ".cache";
    String PROPERTY_PREFIX_SERVLET = PROPERTY_PREFIX_TUTORIALS4J + ".servlet";
    String PROPERTY_PREFIX_SERVLET_CACHE_BODY = PROPERTY_PREFIX_SERVLET + ".cached-body";
}
