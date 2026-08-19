package tutorials4j.framework.examples.template;

import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;

/**
 * 基于计数器缓存模板实现的简单计数器服务，提供基于缓存的计数能力。
 *
 * @author Yun Jiao
 */
@Service
public class SimpleCounterTemplate extends AbstractCounterCacheTemplate {
  /** 构造器，指定计数器缓存名为 counter。 */
  public SimpleCounterTemplate() {
    super("counter");
  }
}
