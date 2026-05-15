package tutorials4j.framework.common.core.json;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.core.Ordered;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface DefaultJackson2ObjectMapperBuilderCustomizer
    extends Jackson2ObjectMapperBuilderCustomizer, Ordered {

  default Module[] toArray(List<Module> modules) {
    if (CollectionUtils.isNotEmpty(modules)) {
      Module[] temps = new Module[modules.size()];
      return modules.toArray(temps);
    } else {
      return new Module[] {};
    }
  }
}
