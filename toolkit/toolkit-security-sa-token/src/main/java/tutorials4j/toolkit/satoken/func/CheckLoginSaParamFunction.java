package tutorials4j.toolkit.satoken.func;

import static tutorials4j.toolkit.satoken.func.FuncOrdered.CHECK_LOGIN;

import cn.dev33.satoken.stp.StpUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CheckLoginSaParamFunction implements OrderedSaParamFunction {

  @Override
  public void run(Object r) {
    StpUtil.checkLogin();
  }

  @Override
  public int getOrder() {
    return CHECK_LOGIN;
  }
}
