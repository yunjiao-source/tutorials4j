package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrategyOrderd.BEFORE_AUTH_CORS;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CorsBeforeSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaResponse response = SaHolder.getResponse();
    response
        .setHeader("Access-Control-Allow-Origin", "*")
        .setHeader("Access-Control-Allow-Methods", "*")
        .setHeader("Access-Control-Allow-Headers", "*")
        .setHeader("Access-Control-Max-Age", "3600");
  }

  @Override
  public AuthFilterPointcutEnum getPointcut() {
    return AuthFilterPointcutEnum.beforeAuth;
  }

  @Override
  public int getOrder() {
    return BEFORE_AUTH_CORS;
  }
}
