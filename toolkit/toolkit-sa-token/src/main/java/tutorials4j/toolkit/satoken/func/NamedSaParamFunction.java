package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.fun.SaParamFunction;
import tutorials4j.toolkit.satoken.util.SaTokenToolkitUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface NamedSaParamFunction extends SaParamFunction<Object> {

  default String getName() {
    return SaTokenToolkitUtils.CHECK_LOGIN_PARAM_FUNCTION;
  }
}
