package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.fun.SaParamFunction;
import org.springframework.core.Ordered;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface OrderedSaParamFunction extends SaParamFunction<Object>, Ordered {

  @Override
  default int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
