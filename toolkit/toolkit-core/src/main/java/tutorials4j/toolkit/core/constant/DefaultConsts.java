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
}
