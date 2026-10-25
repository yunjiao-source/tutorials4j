package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import tutorials4j.toolkit.satoken.util.SaTokenToolkitUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class OptionsMethodSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaRouter.match(SaHttpMethod.OPTIONS).back();
  }

  @Override
  public String getName() {
    return SaTokenToolkitUtils.OPTIONS_METHOD_AUTH_STRATEGY;
  }
}
