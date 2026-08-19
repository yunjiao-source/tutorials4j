package tutorials4j.framework.data.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 数据模块公共配置属性。
 *
 * <p>以 {@code tutorials4j.data} 为前缀绑定外部配置，作为数据模块统一配置入口，目前暂无具体配置项。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA)
public class DataProperties {}
