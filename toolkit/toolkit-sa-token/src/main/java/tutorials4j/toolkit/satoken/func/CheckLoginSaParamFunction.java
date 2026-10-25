package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.stp.StpUtil;
import tutorials4j.toolkit.satoken.util.SaTokenToolkitUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class CheckLoginSaParamFunction implements NamedSaParamFunction {

  @Override
  public void run(Object r) {
    StpUtil.checkLogin();
  }

  @Override
  public String getName() {
    return SaTokenToolkitUtils.CHECK_LOGIN_PARAM_FUNCTION;
  }
}
