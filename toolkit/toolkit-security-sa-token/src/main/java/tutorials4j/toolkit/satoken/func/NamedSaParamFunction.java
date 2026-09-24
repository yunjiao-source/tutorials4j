package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.fun.SaParamFunction;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface NamedSaParamFunction extends SaParamFunction<Object> {

  default String getName() {
    return SaTokenUtils.CHECK_LOGIN_PARAM_FUNCTION;
  }
}
