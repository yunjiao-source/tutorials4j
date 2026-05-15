package tutorials4j.framework.common.core.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.util.WebUtils;
import tutorials4j.framework.common.core.SymbolConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CookieUtils {
  private static Map<String, String> rawCookieToMap(String cookie) {
    if (StringUtils.isNotBlank(cookie)) {
      return Stream.of(cookie.split(SymbolConsts.SEMICOLON_AND_SPACE))
          .map(pair -> pair.split(SymbolConsts.EQUAL))
          .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    } else {
      return Collections.emptyMap();
    }
  }

  public static List<String> get(String cookie, String... name) {
    Map<String, String> cookies = rawCookieToMap(cookie);
    return Stream.of(name).map(cookies::get).toList();
  }

  public static String getAny(String cookie, String... name) {
    List<String> result = get(cookie, name);
    return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
  }

  public static String get(String cookie, String name) {
    Map<String, String> cookies = rawCookieToMap(cookie);
    return cookies.get(name);
  }

  public static Cookie get(HttpServletRequest httpServletRequest, String name) {
    return WebUtils.getCookie(httpServletRequest, name);
  }

  public static String getValue(HttpServletRequest httpServletRequest, String name) {
    Cookie cookie = get(httpServletRequest, name);
    return ObjectUtils.isNotEmpty(cookie) ? cookie.getValue() : null;
  }

  public static String getFromHeader(HttpServletRequest httpServletRequest, String name) {
    String cookie = HeaderUtils.getCookie(httpServletRequest);
    return get(cookie, name);
  }

  public static String getFromHeader(ServerHttpRequest serverHttpRequest, String name) {
    String cookie = HeaderUtils.getCookie(serverHttpRequest);
    return get(cookie, name);
  }

  public static String getFromHeader(HttpInputMessage httpInputMessage, String name) {
    String cookie = HeaderUtils.getCookie(httpInputMessage);
    return get(cookie, name);
  }

  public static String getAnyFromHeader(HttpServletRequest httpServletRequest, String... name) {
    String cookie = HeaderUtils.getCookie(httpServletRequest);
    return getAny(cookie, name);
  }

  public static String getAnyFromHeader(ServerHttpRequest serverHttpRequest, String... name) {
    String cookie = HeaderUtils.getCookie(serverHttpRequest);
    return getAny(cookie, name);
  }

  public static String getAnyFromHeader(HttpInputMessage httpInputMessage, String... name) {
    String cookie = HeaderUtils.getCookie(httpInputMessage);
    return getAny(cookie, name);
  }

  public static void removeCookie(HttpServletResponse response, String key) {
    setCookie(response, key, null, 0);
  }

  public static void setCookie(
      HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setMaxAge(maxAgeInSeconds);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
  }
}
