package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrategyOrderd.BEFORE_AUTH_OPTIONS;

import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class OptionsBeforeSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaRouter.match(SaHttpMethod.OPTIONS).back();
  }

  @Override
  public AuthFilterPointcutEnum getPointcut() {
    return AuthFilterPointcutEnum.beforeAuth;
  }

  @Override
  public int getOrder() {
    return BEFORE_AUTH_OPTIONS;
  }
}
