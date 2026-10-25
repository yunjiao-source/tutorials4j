package tutorials4j.toolkit.core.constant;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface DefaultConsts {

  String REMEMBER_ME = "remember-me";

  String BEARER_TOKEN = "Bearer ";

  List<String> HTTP_HEADER_IP_LIST =
      Arrays.asList(
          "X-Forwarded-For",
          "X-Real-IP",
          "Proxy-Client-IP",
          "WL-Proxy-Client-IP",
          "HTTP_CLIENT_IP",
          "HTTP_X_FORWARDED_FOR");

  String HTTP_HEADER_LOGIN_ID = "X-Login-Id";

  String ADMIN_NAME = "admin";

  String META_TUTORIALS4J_DIR = "META-INF/tutorials4j";

  String LOCKABLE_REDIS_KEY_PREFIX = "lockable:";

  String SESSION_NAME_USER_INFO = "userModel";

  String ROLE_PREFIX = "ROLE_";
}
