package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LoggingSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaManager.getLog().debug("开始认证，path={}", SaHolder.getRequest().getRequestPath());
  }

  @Override
  public String getName() {
    return SaTokenUtils.LOGGING_AUTH_STRATEGY;
  }
}
