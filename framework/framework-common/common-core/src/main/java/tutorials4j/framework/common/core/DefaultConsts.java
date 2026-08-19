package tutorials4j.framework.common.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 框架默认常量定义，集中管理系统属性名、日期格式、字符集及 HTTP 请求头等默认值。
 *
 * @author Yun Jiao
 */
public interface DefaultConsts {
  /** 系统属性名：Worker ID（工作机器ID）。 可通过 JVM 启动参数设置：-DTUTORIALS4J_SNOWFLAKE_WORKER_ID=xxx */
  String WORKER_ID = "TUTORIALS4J_SNOWFLAKE_WORKER_ID";

  /** 系统属性名：Datacenter ID（数据中心ID）。 可通过 JVM 启动参数设置：-DTUTORIALS4J_SNOWFLAKE_DATACENTER_ID=xxx */
  String DATACENTER_ID = "TUTORIALS4J_SNOWFLAKE_DATACENTER_ID";

  /** 时间日期格式 */
  String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** 默认租户代码 */
  String DEFAULT_TENTANT_CODE = "DEFAULT";

  /** 默认字符集 */
  Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  /** MDC 上下文的快照键名 */
  String MDC_CONTEXT_KEY = "mdcSnapshot";

  /** 会话 ID 名称数组 */
  String[] SESSION_IDS = new String[] {"JSESSIONID, SESSION"};

  /** Bearer 认证类型名称 */
  String BEARER_TYPE = "Bearer";

  /** Bearer 认证令牌前缀（含空格） */
  String BEARER_TOKEN = BEARER_TYPE + SymbolConsts.SPACE;

  /** http header 名称定义 */
  String HTTP_HEADER_TENANT = "X-Tenant-Code";

  /** HTTP 头：内部调用标识 */
  String HTTP_HEADER_INNER_CALL = "X-Inner-Call";

  /** HTTP 头：会话 ID */
  String HTTP_HEADER_SESSION_ID = "X-Session-Id";

  /** HTTP 头：开放平台 ID */
  String HTTP_HEADER_OPEN_ID = "X-Open-Id";

  /** HTTP 头：登录账号 */
  String HTTP_HEADER_SIGN_IN_ACCOUNT = "X-Sign-In-Account";

  /** HTTP 头：链路追踪 ID */
  String HTTP_HEADER_TRACE_ID = "X-Trace-Id";

  /** HTTP 头：链路追踪 Span ID */
  String HTTP_HEADER_TRACE_SPAN_ID = "X-Trace-Span-Id";

  /** HTTP 头：链路追踪父 Span ID */
  String HTTP_HEADER_TRACE_PARENT_SPAN_ID = "X-Trace-Parent-Span-Id";

  /** HTTP 头：签名 AppKey */
  String HTTP_HEADER_SIGNATURE_APP_KEY = "X-Signature-App-Key";

  /** HTTP 头：签名时间戳 */
  String HTTP_HEADER_SIGNATURE_TIMESTAMP = "X-Signature-Timestamp";

  /** HTTP 头：签名随机数 */
  String HTTP_HEADER_SIGNATURE_NONCE = "X-Signature-Nonce";

  /** HTTP 头：签名值 */
  String HTTP_HEADER_SIGNATURE = "X-Signature";

  /** HTTP 头：验证码 Key */
  String HTTP_HEADER_CAPTCHA_KEY = "X-Captcha-Key";

  /** HTTP 头：验证码类型 */
  String HTTP_HEADER_CAPTCHA_CATEGORY = "X-Captcha-Category";

  /** HTTP 头：验证码 */
  String HTTP_HEADER_CAPTCHA_CODE = "X-Captcha-Code";

  /** HTTP 头：验证码校验凭据 */
  String HTTP_HEADER_CAPTCHA_AUTH = "X-Captcha-Auth";

  /** HTTP 头：TOTP 校验用户名 */
  String HTTP_HEADER_TOTP_AUTH_USERNAME = "X-Totp-Auth-Username";

  /** HTTP 头：TOTP 校验码 */
  String HTTP_HEADER_TOTP_AUTH_CODE = "X-Totp-Auth-Code";

  /** HTTP 头：TOTP 校验凭据 */
  String HTTP_HEADER_TOTP_AUTH = "X-Totp-Auth";

  /** HTTP 头：加密传输的对称密钥（Hex 格式） */
  String HTTP_HEADER_CRYPTO_SECRET_KEY_HEX = "X-Crypto-Secret-Key-Hex";

  /** Class 名称定义 */
  String CLASS_HIKARI_DATA_SOURCE = "HikariDataSource";

  /** DBCP2 连接池数据源类名 */
  String CLASS_DBCP2_BASIC_DATA_SOURCE = "BasicDataSource";

  /** Druid 连接池数据源类名 */
  String CLASS_DRUID_DATA_SOURCE = "DruidDataSource";

  /** Druid 连接池包装类名 */
  String CLASS_DRUID_DATA_SOURCE_WRAPPER = "DruidDataSourceWrapper";

  /** 其他定义 */
  /** 验证码相关 HTTP 头的组合数组 */
  String[] HTTP_HEADER_CAPTCHA =
      new String[] {
        DefaultConsts.HTTP_HEADER_CAPTCHA_KEY,
        DefaultConsts.HTTP_HEADER_CAPTCHA_CATEGORY,
        DefaultConsts.HTTP_HEADER_CAPTCHA_CODE
      };

  /** 需要写入 MDC 的链路追踪相关 HTTP 头组合数组 */
  String[] HTTP_MDC_KEYS =
      new String[] {
        DefaultConsts.HTTP_HEADER_TRACE_ID,
        DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID,
        DefaultConsts.HTTP_HEADER_TRACE_PARENT_SPAN_ID
      };
}
