package tutorials4j.framework.oss.core.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * OSS 模块配置属性类。
 *
 * <p>绑定前缀为 {@code PropertiesConsts.PROPERTY_PREFIX_OSS} 的配置项。 包含嵌套的指标配置 {@link MetricsOptions}。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_OSS)
public class OssProperties {
  /** 指标相关配置，以 {@code metrics} 为前缀 */
  @NestedConfigurationProperty private final MetricsOptions metrics = new MetricsOptions();
}
