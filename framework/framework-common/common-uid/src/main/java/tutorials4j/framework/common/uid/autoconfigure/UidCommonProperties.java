package tutorials4j.framework.common.uid.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 框架 UID 生成器通用配置属性。
 *
 * <p>对应配置前缀 {@code PropertiesConsts.PROPERTY_PREFIX_COMMON_UID}， 用于配置雪花算法的时间位、工作机器位、序列号位以及纪元起始时间。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_COMMON_UID)
public class UidCommonProperties {
  /** 雪花算法时间戳占用的位数。 */
  private Integer timeBits = 28;

  /** 雪花算法工作机器 ID 占用的位数。 */
  private Integer workerBits = 22;

  /** 雪花算法同一毫秒内序列号占用的位数。 */
  private Integer seqBits = 13;

  /** 雪花算法纪元起始时间字符串（默认 2025-08-20）。 */
  private String epochStr = "2025-08-20";
}
