package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrategyOrderd.AUTH_LOGGING;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LoggingSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaManager.getLog().debug("开始认证，path={}", SaHolder.getRequest().getRequestPath());
  }

  @Override
  public int getOrder() {
    return AUTH_LOGGING;
  }
}
