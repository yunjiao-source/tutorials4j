package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrageyOrderd.CHECK_LOGIN;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CheckLoginSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaRouter.match(
            "/**",
            r -> {
              StpUtil.checkLogin();
            })
        .stop();
  }

  @Override
  public int getOrder() {
    return CHECK_LOGIN;
  }
}
