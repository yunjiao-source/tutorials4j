package tutorials4j.service.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.service.ai.support.ModelType;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_SERVICE_AI)
public class AIServiceProperties {
  private ModelType defaultModelType = ModelType.deepseek;
}
