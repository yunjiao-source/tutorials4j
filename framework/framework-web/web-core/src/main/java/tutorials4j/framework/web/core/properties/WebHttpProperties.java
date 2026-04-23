package tutorials4j.framework.web.core.properties;

import jakarta.servlet.DispatcherType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.util.EnumSet;

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
    private CachedRequestBody cachedRequestBody = new CachedRequestBody();

    /**
     * 缓存请求体配置属性
     */
    @Data
    public static class CachedRequestBody {
        /**
         * 是否开启
         */
        private boolean enabled = false;

        /**
         * 匹配url地址
         */
        private String[] urlPatterns = new String[]{"/*"};

        /**
         * 过滤器启动排序，数值越小越先执行
         */
        private Integer order = 1;

        /**
         * 过滤器名称
         */
        private String name = "defaultCachedBodyFilter";

        private EnumSet<DispatcherType> dispatcherTypes = EnumSet.allOf(DispatcherType.class);

        /**
         * 支持最大长度，默认:2M
         */
        private DataSize maxContentLength = DataSize.ofMegabytes(2);
    }
}
