package tutorials4j.framework.oss.core.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.MetricsOptions;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.RandomFolderOptions;

/**
 * OSS模块配置属性类，绑定前缀为 {@code PropertiesConsts.PROPERTY_PREFIX_OSS}。
 *
 * <p>包含随机目录生成选项（{@link RandomFolderOptions}）和指标配置（{@link MetricsOptions}）。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_OSS)
public class OssProperties {

  /** 随机目录生成配置 */
  @NestedConfigurationProperty
  private final RandomFolderOptions randomFolder = new RandomFolderOptions();

  /** 指标相关配置（如监控埋点） */
  @NestedConfigurationProperty private final MetricsOptions metrics = new MetricsOptions();
}
