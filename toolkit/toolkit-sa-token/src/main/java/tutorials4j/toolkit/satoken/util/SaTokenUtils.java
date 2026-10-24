package tutorials4j.toolkit.satoken.util;

import static tutorials4j.toolkit.core.constant.DefaultConsts.REMEMBER_ME;
import static tutorials4j.toolkit.core.constant.ToolkitConsts.AUTHORITY_ROLE_PREFIX;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.core.exception.UnauthorizedException;
import tutorials4j.toolkit.core.model.UserModel;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface SaTokenUtils {
  String BLOCK_URLS_AUTH_STRATEGY = "blockUrls";
  String CHECK_LOGIN_AUTH_STRATEGY = "checkLogin";
  String LOGGING_AUTH_STRATEGY = "logging";
  String SAME_TOKEN_AUTH_STRATEGY = "sameToken";
  String WHITE_URL_AUTH_STRATEGY = "whiteUrls";
  String CORS_AUTH_STRATEGY = "cors";
  String OPTIONS_METHOD_AUTH_STRATEGY = "optionsMethod";

  String CHECK_LOGIN_PARAM_FUNCTION = "checkLogin";

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

  static <T> T getFromSession(Object loginId, String name, Class<T> clazz) {
    var o = StpUtil.getSessionByLoginId(loginId).get(name);

    if (o == null) {
      return null;
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

  static UserModel getUserFromSession() {
    Object loginId = StpUtil.getLoginId();
    return getFromSession(loginId, ToolkitConsts.SESSION_NAME_USER_INFO, UserModel.class);
  }

  static UserModel getUserFromSession(Object loginId) {
    return getFromSession(loginId, ToolkitConsts.SESSION_NAME_USER_INFO, UserModel.class);
  }

  static boolean extractRememberMe() {
    var saRequest = SaHolder.getRequest();
    var value = saRequest.getParam(REMEMBER_ME);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    value = saRequest.getHeader(REMEMBER_ME);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    return false;
  }

  static Set<String> getGrantedAuthorities() {
    String loginId = StpUtil.getLoginId("");
    if (StringUtils.isNotBlank(loginId)) {
      return getGrantedAuthorities(loginId);
    }

    return new HashSet<>(0);
  }

  static Set<String> getGrantedAuthorities(String loginId) {
    List<String> roleAuthorities =
        StpUtil.getRoleList(loginId).stream().map(e -> AUTHORITY_ROLE_PREFIX + e).toList();
    Set<String> grantedAuthorities = new HashSet<>(roleAuthorities);

    List<String> permissionAuthorities = StpUtil.getPermissionList(loginId).stream().toList();
    grantedAuthorities.addAll(permissionAuthorities);

    return grantedAuthorities;
  }
}
