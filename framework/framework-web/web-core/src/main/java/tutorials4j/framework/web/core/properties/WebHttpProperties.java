package tutorials4j.framework.web.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.unit.DataSize;
import tutorials4j.framework.common.core.PropertiesConsts;

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
    private CachedRequest cachedRequest = new CachedRequest();

    /**
     * 链路追踪 Servlet 过滤器配置属性。
     */
    @NestedConfigurationProperty
    private ServletFilterOptions trace = new ServletFilterOptions();

    {
        trace.setName("traceServletFilter");
        cachedRequest.setUrlPatterns(new String[]{"/cached-request/*"});
        cachedRequest.setName("cachedRequestServletFilter");
    }

    /**
     * 缓存请求体配置属性
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CachedRequest extends ServletFilterOptions {
        /**
         * 支持最大长度，默认:2M
         */
        private DataSize maxContentLength = DataSize.ofMegabytes(2);
    }
}
