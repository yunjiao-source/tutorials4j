package tutorials4j.framework.web.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.unit.DataSize;
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
     * 缓存请求体配置属性，用于 {@code CachedRequestServletFilter}。
     */
    private CachedRequestOptions cachedRequest = new CachedRequestOptions("cachedRequestServletFilter");

    /**
     * 链路追踪 Servlet 过滤器配置属性。
     */
    @NestedConfigurationProperty
    private ServletFilterOptions trace = new ServletFilterOptions("traceServletFilter");

    /**
     * xss攻击配置
     */
    @NestedConfigurationProperty
    private ServletFilterOptions xss = new ServletFilterOptions("xssServletFilter");

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
     * 缓存请求体配置属性
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CachedRequestOptions extends ServletFilterOptions {
        public CachedRequestOptions(String name) {
            super(name);
        }

        /**
         * 支持最大长度，默认:2M
         */
        private DataSize maxContentLength = DataSize.ofMegabytes(2);
    }
}
