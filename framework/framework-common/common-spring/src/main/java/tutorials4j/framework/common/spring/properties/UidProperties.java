package tutorials4j.framework.common.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_COMMON_UID)
public class UidProperties {
  private Integer timeBits = 28;

  private Integer workerBits = 22;

  private Integer seqBits = 13;

  private String epochStr = "2025-08-20";
}
