package tutorials4j.toolkit.satoken.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface SaTokenUtils {
  String REMEMBER_ME_VALUE = "remember-me";

  static <T> T getLoginId(Class<T> clazz) {
    var o = StpUtil.getLoginId();

    if (o == null) {
      throw new UnauthorizedException();
    }
    // 如果类型已经匹配，直接返回
    if (clazz.isInstance(o)) {
      return clazz.cast(o);
    }
    // 否则进行类型转换（例如 String -> Long/Integer）
    var str = o.toString();
    if (clazz == String.class) {
      return clazz.cast(str);
    }
    if (clazz == Long.class) {
      return clazz.cast(Long.valueOf(str));
    }
    if (clazz == Integer.class) {
      return clazz.cast(Integer.valueOf(str));
    }
    // 兜底：尝试直接强转
    return clazz.cast(o);
  }

  static String getLoginIdStr() {
    return getLoginId(String.class);
  }

  static <T> T getSession(String name, Class<T> clazz) {
    var o = StpUtil.getSession().get(name);

    if (o == null) {
      throw new UnauthorizedException();
    }

    if (clazz.isInstance(o)) {
      return clazz.cast(o);
    }

    throw new ClassCastException(
        "session 属性 ["
            + name
            + "] 类型不匹配, 期望: "
            + clazz.getName()
            + ", 实际: "
            + o.getClass().getName());
  }

  static boolean extractRememberMe() {
    var saRequest = SaHolder.getRequest();
    var value = saRequest.getParam(REMEMBER_ME_VALUE);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    value = saRequest.getHeader(REMEMBER_ME_VALUE);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    return false;
  }
}
