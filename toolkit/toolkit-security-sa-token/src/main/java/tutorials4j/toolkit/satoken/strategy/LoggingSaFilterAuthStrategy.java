package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrageyOrderd.LOGGING;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LoggingSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaManager.getLog()
        .debug(
            "开始认证，path={}, token={}",
            SaHolder.getRequest().getRequestPath(),
            StpUtil.getTokenValue());
  }

  @Override
  public int getOrder() {
    return LOGGING;
  }
}
