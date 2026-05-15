package tutorials4j.framework.common.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 默认常量定义
 *
 * @author Yun Jiao
 */
public interface DefaultConsts {
  /** 时间日期格式 */
  String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** 默认租户代码 */
  String DEFAULT_TENTANT_CODE = "DEFAULT";

  /** 默认字符集 */
  Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  String MDC_CONTEXT_KEY = "mdcSnapshot";

  String[] SESSION_IDS = new String[] {"JSESSIONID, SESSION"};

  String BEARER_TYPE = "Bearer";
  String BEARER_TOKEN = BEARER_TYPE + SymbolConsts.SPACE;

  /** http header 名称定义 */
  String HTTP_HEADER_TENANT = "X-Tenant-Code";

  String HTTP_HEADER_INNER_CALL = "X-Inner-Call";
  String HTTP_HEADER_SESSION_ID = "X-Session-Id";
  String HTTP_HEADER_OPEN_ID = "X-Open-Id";
  String HTTP_TRACE_ID = "X-Trace-Id";
  String HTTP_TRACE_SPAN_ID = "X-Trace-Span-Id";
  String HTTP_TRACE_PARENT_SPAN_ID = "X-Trace-Parent-Span-Id";
  String HTTP_SIGNATURE_APP_KEY = "X-Signature-App-Key";
  String HTTP_SIGNATURE_TIMESTAMP = "X-Signature-Timestamp";
  String HTTP_SIGNATURE_NONCE = "X-Signature-Nonce";
  String HTTP_SIGNATURE = "X-Signature";

  /** Class 名称定义 */
  String CLASS_HIKARI_DATA_SOURCE = "HikariDataSource";

  String CLASS_DBCP2_BASIC_DATA_SOURCE = "BasicDataSource";
  String CLASS_DRUID_DATA_SOURCE = "DruidDataSource";
  String CLASS_DRUID_DATA_SOURCE_WRAPPER = "DruidDataSourceWrapper";

  /** 其他定义 */
  String[] HTTP_MDC_KEYS =
      new String[] {
        DefaultConsts.HTTP_TRACE_ID,
        DefaultConsts.HTTP_TRACE_SPAN_ID,
        DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID
      };
}
