package tutorials4j.framework.web.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB)
public class WebProperties {

    private ClientOptions client = new ClientOptions();

    @Data
    public static class ClientOptions {
        /**
         * 默认请求头
         */
        private Map<String, String> defaultHeaders = new HashMap<>();

    }

}
