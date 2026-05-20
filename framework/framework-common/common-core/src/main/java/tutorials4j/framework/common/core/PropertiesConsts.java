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

  String PROPERTY_PREFIX_CACHE_NAMED = PROPERTY_PREFIX_CACHE + ".named";
  String PROPERTY_PREFIX_CACHE_REDIS = PROPERTY_PREFIX_CACHE + ".redis";
  String PROPERTY_PREFIX_CACHE_CAFFEINE = PROPERTY_PREFIX_CACHE + ".caffeine";

  /** captcha */
  String PROPERTY_PREFIX_CAPTCHA = PROPERTY_PREFIX_TUTORIALS4J + ".captcha";

  String PROPERTY_PREFIX_CAPTCHA_HUTOOL = PROPERTY_PREFIX_CAPTCHA + ".hutool";
  String PROPERTY_PREFIX_CAPTCHA_TIANAI = PROPERTY_PREFIX_CAPTCHA + ".tianai";

  /** web */
  String PROPERTY_PREFIX_WEB = PROPERTY_PREFIX_TUTORIALS4J + ".web";

  String PROPERTY_PREFIX_WEB_FILTER = PROPERTY_PREFIX_WEB + ".filter";

  String PROPERTY_PREFIX_WEB_INTERCEPTOR = PROPERTY_PREFIX_WEB + ".interceptor";

  String PROPERTY_PREFIX_WEB_CLIENT = PROPERTY_PREFIX_WEB + ".client";

  /** data */
  String PROPERTY_PREFIX_DATA = PROPERTY_PREFIX_TUTORIALS4J + ".data";

  String PROPERTY_PREFIX_DATA_MYBATIS_PLUS = PROPERTY_PREFIX_TUTORIALS4J + ".mybatis-plus";

  /** tenant */
  String PROPERTY_PREFIX_TENANT = PROPERTY_PREFIX_TUTORIALS4J + ".tenant";
}
