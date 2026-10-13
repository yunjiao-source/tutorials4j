package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.stp.StpUtil;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class CheckLoginSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    StpUtil.checkLogin();
  }

  @Override
  public String getName() {
    return SaTokenUtils.CHECK_LOGIN_AUTH_STRATEGY;
  }
}
