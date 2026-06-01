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
  /** common */
  String PROPERTY_PREFIX_COMMON = PROPERTY_PREFIX_TUTORIALS4J + ".common";

  String PROPERTY_PREFIX_COMMON_UID = PROPERTY_PREFIX_COMMON + ".uid";

  /** assembly */
  String PROPERTY_PREFIX_ASSY = PROPERTY_PREFIX_TUTORIALS4J + ".assy";

  String PROPERTY_PREFIX_ASSY_SCHEDULE = PROPERTY_PREFIX_ASSY + ".schedule";

  /** cache */
  String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_TUTORIALS4J + ".cache";

  String PROPERTY_PREFIX_CACHE_NAMED = PROPERTY_PREFIX_CACHE + ".named";

  /** crypto */
  String PROPERTY_PREFIX_CRYPTO = PROPERTY_PREFIX_TUTORIALS4J + ".crypto";

  /** captcha */
  String PROPERTY_PREFIX_CAPTCHA = PROPERTY_PREFIX_TUTORIALS4J + ".captcha";

  String PROPERTY_PREFIX_CAPTCHA_HUTOOL = PROPERTY_PREFIX_CAPTCHA + ".hutool";
  String PROPERTY_PREFIX_CAPTCHA_TIANAI = PROPERTY_PREFIX_CAPTCHA + ".tianai";

  /** web */
  String PROPERTY_PREFIX_WEB = PROPERTY_PREFIX_TUTORIALS4J + ".web";

  String PROPERTY_PREFIX_WEB_REST = PROPERTY_PREFIX_WEB + ".rest";

  String PROPERTY_PREFIX_WEB_LOGGING = PROPERTY_PREFIX_WEB + ".logging";

  String PROPERTY_PREFIX_WEB_CLIENT = PROPERTY_PREFIX_WEB + ".client";

  String PROPERTY_PREFIX_WEB_SECURITY = PROPERTY_PREFIX_WEB + ".security";

  String PROPERTY_PREFIX_WEB_GOOGLE = PROPERTY_PREFIX_WEB_SECURITY + ".google";

  /** data */
  String PROPERTY_PREFIX_DATA = PROPERTY_PREFIX_TUTORIALS4J + ".data";

  String PROPERTY_PREFIX_DATA_MYBATIS_PLUS = PROPERTY_PREFIX_TUTORIALS4J + ".mybatis-plus";

  /** tenant */
  String PROPERTY_PREFIX_TENANT = PROPERTY_PREFIX_TUTORIALS4J + ".tenant";
}
