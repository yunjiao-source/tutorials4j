package tutorials4j.framework.common.lang;

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
    String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_TUTORIALS4J + ".cache";
    String PROPERTY_PREFIX_WEB = PROPERTY_PREFIX_TUTORIALS4J + ".web";
    String PROPERTY_PREFIX_DATA = PROPERTY_PREFIX_TUTORIALS4J + ".data";
}
