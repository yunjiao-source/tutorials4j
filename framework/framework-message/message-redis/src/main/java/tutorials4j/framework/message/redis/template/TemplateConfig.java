package tutorials4j.framework.message.redis.template;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record TemplateConfig(String name, String queueName) {

  public void validate() {
    if (StringUtils.isAnyBlank(name, queueName)) {
      throw new IllegalArgumentException("config properties must not be null or empty");
    }
  }
}
