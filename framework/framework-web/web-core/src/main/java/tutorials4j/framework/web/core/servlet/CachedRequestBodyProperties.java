package tutorials4j.framework.web.core.servlet;

import jakarta.servlet.DispatcherType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import tutorials4j.framework.web.core.constant.WebPropertiesConsts;

import java.util.EnumSet;

/**
 * 缓存请求体配置属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CACHED_REQUEST_BODY)
public class CachedRequestBodyProperties {
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
