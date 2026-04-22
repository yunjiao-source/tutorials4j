package tutorials4j.framework.web.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.lang.PropertiesConsts;

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
     * 是否开启日志。默认：false
     */
    private boolean loggerEnabled = false;

    /**
     * 缓存客户端请求，响应，可重复读取。默认：false
     */
    private boolean bufferingClientHttpRequestEnabled = false;

    /**
     * 基础url
     */
    private String baseUrl;

    /**
     * 默认请求头
     */
    private Map<String, String> defaultHeaders = new HashMap<>();


}
