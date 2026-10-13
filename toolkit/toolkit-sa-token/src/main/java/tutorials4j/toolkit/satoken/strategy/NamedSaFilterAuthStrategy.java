package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.filter.SaFilterAuthStrategy;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface NamedSaFilterAuthStrategy extends SaFilterAuthStrategy {

  default String getName() {
    return "";
  }
}
