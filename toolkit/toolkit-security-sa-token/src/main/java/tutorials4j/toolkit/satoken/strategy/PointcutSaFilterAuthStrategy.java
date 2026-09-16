package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import org.springframework.core.Ordered;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface PointcutSaFilterAuthStrategy extends SaFilterAuthStrategy, Ordered {
  default AuthFilterPointcutEnum getPointcut() {
    return AuthFilterPointcutEnum.auth;
  }

  @Override
  default int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
