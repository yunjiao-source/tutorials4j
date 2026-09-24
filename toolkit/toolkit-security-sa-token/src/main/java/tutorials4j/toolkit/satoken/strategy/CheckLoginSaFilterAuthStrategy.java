package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CheckLoginSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

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
  public String getName() {
    return SaTokenUtils.CHECK_LOGIN_AUTH_STRATEGY;
  }
}
