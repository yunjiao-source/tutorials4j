package tutorials4j.framework.web.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * web客户端配置属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT)
public class WebClientProperties {
    /**
     * 默认请求头
     */
    private Map<String, String> defaultHeaders = new HashMap<>();

}
