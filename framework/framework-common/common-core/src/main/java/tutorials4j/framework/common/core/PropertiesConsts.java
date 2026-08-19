package tutorials4j.framework.common.core;

/**
 * 基础属性配置前缀常量。
 *
 * <p>统一管理框架各模块在 application 配置文件中的属性前缀（tutorials4j.*）。
 *
 * @author Yun Jiao
 */
public interface PropertiesConsts {
  /** 功能开关属性名 */
  String PROPERTY_ENABLED = "enabled";

  /** Spring 配置前缀 */
  String PROPERTY_PREFIX_SPRING = "spring";

  /** 框架自定义配置根前缀 */
  String PROPERTY_PREFIX_TUTORIALS4J = "tutorials4j";

  /* ---------- 自定义配置属性 ---------- */
  /** common */
  String PROPERTY_PREFIX_COMMON = PROPERTY_PREFIX_TUTORIALS4J + ".common";

  /** common.uid 配置前缀 */
  String PROPERTY_PREFIX_COMMON_UID = PROPERTY_PREFIX_COMMON + ".uid";

  /** assembly */
  String PROPERTY_PREFIX_ASSEMBLY = PROPERTY_PREFIX_TUTORIALS4J + ".assembly";

  /** schedule */
  String PROPERTY_PREFIX_SCHEDULE = PROPERTY_PREFIX_TUTORIALS4J + ".schedule";

  /** schedule.spring 配置前缀 */
  String PROPERTY_PREFIX_SCHEDULE_SPRING = PROPERTY_PREFIX_SCHEDULE + ".spring";

  /** schedule.powerjob.worker 配置前缀 */
  String PROPERTY_PREFIX_SCHEDULE_POWERJOB_WORKER = PROPERTY_PREFIX_SCHEDULE + ".powerjob.worker";

  /** schedule.xxl-job 配置前缀 */
  String PROPERTY_PREFIX_SCHEDULE_XXL_JOB = PROPERTY_PREFIX_SCHEDULE + ".xxl-job";

  /** cache */
  String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_TUTORIALS4J + ".cache";

  /** cache.lock 配置前缀 */
  String PROPERTY_PREFIX_CACHE_LOCK = PROPERTY_PREFIX_CACHE + ".lock";

  /** cache.named 配置前缀 */
  String PROPERTY_PREFIX_CACHE_NAMED = PROPERTY_PREFIX_CACHE + ".named";

  /** crypto */
  String PROPERTY_PREFIX_CRYPTO = PROPERTY_PREFIX_TUTORIALS4J + ".crypto";

  /** crypto.web 配置前缀 */
  String PROPERTY_PREFIX_CRYPTO_WEB = PROPERTY_PREFIX_CRYPTO + ".web";

  /** captcha */
  String PROPERTY_PREFIX_CAPTCHA = PROPERTY_PREFIX_TUTORIALS4J + ".captcha";

  /** captcha.hutool 配置前缀 */
  String PROPERTY_PREFIX_CAPTCHA_HUTOOL = PROPERTY_PREFIX_CAPTCHA + ".hutool";

  /** captcha.tianai 配置前缀 */
  String PROPERTY_PREFIX_CAPTCHA_TIANAI = PROPERTY_PREFIX_CAPTCHA + ".tianai";

  /** captcha.web 配置前缀 */
  String PROPERTY_PREFIX_CAPTCHA_WEB = PROPERTY_PREFIX_CAPTCHA + ".web";

  /** web */
  String PROPERTY_PREFIX_WEB = PROPERTY_PREFIX_TUTORIALS4J + ".web";

  /** web.trace 配置前缀 */
  String PROPERTY_PREFIX_WEB_TRACE = PROPERTY_PREFIX_WEB + ".trace";

  /** web.xss 配置前缀 */
  String PROPERTY_PREFIX_WEB_XSS = PROPERTY_PREFIX_WEB + ".xss";

  /** web.signature 配置前缀 */
  String PROPERTY_PREFIX_WEB_SIGNATURE = PROPERTY_PREFIX_WEB + ".signature";

  /** web.totp 配置前缀 */
  String PROPERTY_PREFIX_WEB_TOTP = PROPERTY_PREFIX_WEB + ".totp";

  /** web.cached-body 配置前缀 */
  String PROPERTY_PREFIX_WEB_CACHED_BODY = PROPERTY_PREFIX_WEB + ".cached-body";

  /** web.request-logging 配置前缀 */
  String PROPERTY_PREFIX_WEB_REQUEST_LOGGING = PROPERTY_PREFIX_WEB + ".request-logging";

  /** web.client 配置前缀 */
  String PROPERTY_PREFIX_WEB_CLIENT = PROPERTY_PREFIX_WEB + ".client";

  /** web.security 配置前缀 */
  String PROPERTY_PREFIX_WEB_SECURITY = PROPERTY_PREFIX_WEB + ".security";

  /** data */
  String PROPERTY_PREFIX_DATA = PROPERTY_PREFIX_TUTORIALS4J + ".data";

  /** data.mybatis-plus 配置前缀 */
  String PROPERTY_PREFIX_DATA_MYBATIS_PLUS = PROPERTY_PREFIX_DATA + ".mybatis-plus";

  /** data.hibernate 配置前缀 */
  String PROPERTY_PREFIX_DATA_HIBERNATE = PROPERTY_PREFIX_DATA + ".hibernate";

  /** tenant */
  String PROPERTY_PREFIX_TENANT = PROPERTY_PREFIX_TUTORIALS4J + ".tenant";

  /** message */
  String PROPERTY_PREFIX_MESSAGE = PROPERTY_PREFIX_TUTORIALS4J + ".message";

  /** message.redis 配置前缀 */
  String PROPERTY_PREFIX_MESSAGE_REDIS = PROPERTY_PREFIX_MESSAGE + ".redis";

  /** feature */
  String PROPERTY_PREFIX_FEATURE = PROPERTY_PREFIX_TUTORIALS4J + ".feature";

  /** feature.sign-in 配置前缀 */
  String PROPERTY_PREFIX_FEATURE_SIGN_IN = PROPERTY_PREFIX_FEATURE + ".sign-in";
}
