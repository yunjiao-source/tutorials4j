package tutorials4j.framework.message.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 消息模块配置属性。
 *
 * <p>以 {@code tutorials4j.message} 为前缀绑定外部配置，作为消息模块统一配置入口，目前暂无具体配置项。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_MESSAGE)
public class MessageProperties {}
