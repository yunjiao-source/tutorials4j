package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class OptionsMethodSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaRouter.match(SaHttpMethod.OPTIONS).back();
  }

  @Override
  public String getName() {
    return SaTokenUtils.OPTIONS_METHOD_AUTH_STRATEGY;
  }
}
