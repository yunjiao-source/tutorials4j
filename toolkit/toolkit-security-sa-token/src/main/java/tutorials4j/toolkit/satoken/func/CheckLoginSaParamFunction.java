package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.stp.StpUtil;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CheckLoginSaParamFunction implements NamedSaParamFunction {

  @Override
  public void run(Object r) {
    StpUtil.checkLogin();
  }

  @Override
  public String getName() {
    return SaTokenUtils.CHECK_LOGIN_PARAM_FUNCTION;
  }
}
