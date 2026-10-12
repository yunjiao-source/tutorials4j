package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.util.SaTokenUtils.SAME_TOKEN_AUTH_STRATEGY;

import cn.dev33.satoken.same.SaSameUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SameTokenSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaSameUtil.checkCurrentRequestToken();
  }

  @Override
  public String getName() {
    return SAME_TOKEN_AUTH_STRATEGY;
  }
}
