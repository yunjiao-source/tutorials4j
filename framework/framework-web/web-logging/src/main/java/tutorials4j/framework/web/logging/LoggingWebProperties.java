package tutorials4j.framework.web.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_LOGGING)
public class LoggingWebProperties {
  /** 链路追踪 Servlet 过滤器配置属性。 */
  @NestedConfigurationProperty
  private ServletFilterOptions trace =
      new ServletFilterOptions(
          new String[] {}, 1, "traceRequestFilter", ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);

  /** 请求日志配置 */
  private RequestOptions request = new RequestOptions();

  /** 请求日志相关配置选项。 */
  @Data
  public static class RequestOptions {
    /** Servlet 过滤器通用配置，如过滤器名称、启用状态、URL 匹配模式等。 */
    @NestedConfigurationProperty
    private ServletFilterOptions filter =
        new ServletFilterOptions(
            new String[] {},
            1,
            "requestLoggingRequestFilter",
            ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);

    /** 是否在日志消息中包含时间戳。 */
    private boolean includeTimestamp = false;

    /** 是否包含查询字符串（已由父类支持，此字段预留）。 */
    private boolean includeQueryString = false;

    /** 是否包含客户端信息（IP、会话ID、用户等）。 */
    private boolean includeClientInfo = false;

    /** 是否包含请求头信息。 */
    private boolean includeHeaders = false;

    /** 是否包含请求体 payload。 */
    private boolean includePayload = false;

    /** 记录 payload 的最大长度（字符数），超出部分将被截断。 */
    private int maxPayloadLength = 300;

    /** 请求前日志消息的前缀。 */
    private String beforeMessagePrefix = "请求前 [";

    /** 请求前日志消息的后缀。 */
    private String beforeMessageSuffix = "]";

    /** 请求后日志消息的前缀。 */
    private String afterMessagePrefix = "请求后 [";

    /** 请求后日志消息的后缀。 */
    private String afterMessageSuffix = "]";
  }
}
