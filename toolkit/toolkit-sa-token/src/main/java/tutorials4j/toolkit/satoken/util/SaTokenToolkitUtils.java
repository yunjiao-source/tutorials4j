package tutorials4j.toolkit.satoken.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.stp.StpUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.exception.UnauthorizedException;
import tutorials4j.toolkit.core.model.UserModel;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface SaTokenToolkitUtils {
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
    return getFromSession(loginId, DefaultConsts.SESSION_NAME_USER_INFO, UserModel.class);
  }

  static UserModel getUserFromSession(Object loginId) {
    return getFromSession(loginId, DefaultConsts.SESSION_NAME_USER_INFO, UserModel.class);
  }

  static boolean extractRememberMe() {
    var saRequest = SaHolder.getRequest();
    var value = saRequest.getParam(DefaultConsts.REMEMBER_ME);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    value = saRequest.getHeader(DefaultConsts.REMEMBER_ME);
    if (StringUtils.isNotBlank(value)) {
      return Boolean.parseBoolean(value.trim());
    }

    return false;
  }

  static List<String> getPermissionList() {
    String loginId = StpUtil.getLoginId("");
    if (StringUtils.isNotBlank(loginId)) {
      return getPermissionList(loginId);
    }

    return new ArrayList<>(0);
  }

  static List<String> getPermissionList(String loginId) {
    return StpUtil.getPermissionList(loginId);
  }

  static List<String> getRoleList() {
    String loginId = StpUtil.getLoginId("");
    if (StringUtils.isNotBlank(loginId)) {
      return getRoleList(loginId);
    }

    return new ArrayList<>(0);
  }

  static List<String> getRoleList(String loginId) {
    return StpUtil.getRoleList(loginId);
  }

  static List<GrantedAuthority> getGrantedAuthorityList() {
    String loginId = StpUtil.getLoginId("");
    if (StringUtils.isNotBlank(loginId)) {
      return getGrantedAuthorityList(loginId);
    }

    return new ArrayList<>(0);
  }

  static List<GrantedAuthority> getGrantedAuthorityList(String loginId) {
    List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();

    getRoleList(loginId)
        .forEach(
            code -> {
              GrantedAuthority grantedAuthority =
                  new SimpleGrantedAuthority(DefaultConsts.ROLE_PREFIX + code);
              grantedAuthorityList.add(grantedAuthority);
            });

    getPermissionList(loginId)
        .forEach(
            code -> {
              GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(code);
              grantedAuthorityList.add(grantedAuthority);
            });

    return grantedAuthorityList;
  }

  interface OAuth2 {
    static String getAccessToken() {
      return SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
    }

    static Object getLoginIdByAccessToken(String accessToken) {
      return SaOAuth2Util.getLoginIdByAccessToken(accessToken);
    }

    static AccessTokenModel getAccessToken(String accessToken) {
      return SaOAuth2Util.getAccessToken(accessToken);
    }
  }
}
