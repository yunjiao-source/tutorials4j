package tutorials4j.framework.examples.template;

import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;

/**
 * 简单计数器
 *
 * @author Yun Jiao
 */
@Service
public class SimpleCounterTemplate extends AbstractCounterCacheTemplate {
  public SimpleCounterTemplate() {
    super("counter");
  }
}
