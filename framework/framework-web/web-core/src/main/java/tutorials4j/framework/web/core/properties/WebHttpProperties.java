package tutorials4j.framework.web.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.support.HandlerInterceptorOptions;
import tutorials4j.framework.common.core.support.ServletFilterOptions;

/**
 * Web Http 属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP)
public class WebHttpProperties {
    /**
     * 缓存请求体配置属性
     */
    @NestedConfigurationProperty
    private ServletFilterOptions cachedBody = new ServletFilterOptions();

    /**
     * 链路追踪 Servlet 过滤器配置属性。
     */
    @NestedConfigurationProperty
    private ServletFilterOptions trace = new ServletFilterOptions();

    /**
     * xss攻击配置
     */
    @NestedConfigurationProperty
    private ServletFilterOptions xss = new ServletFilterOptions();

    /**
     * 幂等配置
     */
    @NestedConfigurationProperty
    private HandlerInterceptorOptions idempotent = new HandlerInterceptorOptions();

    /**
     * 访问限制
     */
    @NestedConfigurationProperty
    private HandlerInterceptorOptions accessLimited = new HandlerInterceptorOptions();

    /**
     * 请求日志配置
     */
    private RequestLoggingOptions requestLogging = new RequestLoggingOptions();

    /**
     * 请求日志相关配置选项。
     */
    @Data
    public static class RequestLoggingOptions {
        /**
         * Servlet 过滤器通用配置，如过滤器名称、启用状态、URL 匹配模式等。
         */
        @NestedConfigurationProperty
        private ServletFilterOptions filter = new ServletFilterOptions();

        /**
         * 是否在日志消息中包含时间戳。
         */
        private boolean includeTimestamp = false;

        /**
         * 是否包含查询字符串（已由父类支持，此字段预留）。
         */
        private boolean includeQueryString = false;

        /**
         * 是否包含客户端信息（IP、会话ID、用户等）。
         */
        private boolean includeClientInfo = false;

        /**
         * 是否包含请求头信息。
         */
        private boolean includeHeaders = false;

        /**
         * 是否包含请求体 payload。
         */
        private boolean includePayload = false;

        /**
         * 记录 payload 的最大长度（字符数），超出部分将被截断。
         */
        private int maxPayloadLength = 300;

        /**
         * 请求前日志消息的前缀。
         */
        private String beforeMessagePrefix = "请求前 [";

        /**
         * 请求前日志消息的后缀。
         */
        private String beforeMessageSuffix = "]";

        /**
         * 请求后日志消息的前缀。
         */
        private String afterMessagePrefix = "请求后 [";

        /**
         * 请求后日志消息的后缀。
         */
        private String afterMessageSuffix = "]";

        {
            filter.setName("requestLoggingRequestFilter");
        }
    }


    {
        cachedBody.setName("cachedBodyRequestFilter");
        trace.setName("traceRequestFilter");
        xss.setName("xssRequestFilter");
    }
}
