package tutorials4j.framework.common.spring.util;

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
 * Cookie 工具类，提供从 Cookie 字符串、请求头或请求对象中读取 Cookie 值，以及写入/删除 Cookie 的能力。
 *
 * @author Yun Jiao
 */
public class CookieUtils {
  /** 将原始 Cookie 字符串解析为名称到值的映射；字符串为空时返回空映射。 */
  private static Map<String, String> rawCookieToMap(String cookie) {
    if (StringUtils.isNotBlank(cookie)) {
      return Stream.of(cookie.split(SymbolConsts.SEMICOLON_AND_SPACE))
          .map(pair -> pair.split(SymbolConsts.EQUAL))
          .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    } else {
      return Collections.emptyMap();
    }
  }

  /**
   * 按名称列表从 Cookie 字符串中获取对应的值列表。
   *
   * @param cookie Cookie 字符串
   * @param name 一个或多个 Cookie 名称
   * @return 与名称顺序对应的值列表（不存在的名称为 null）
   */
  public static List<String> get(String cookie, String... name) {
    Map<String, String> cookies = rawCookieToMap(cookie);
    return Stream.of(name).map(cookies::get).toList();
  }

  /**
   * 从 Cookie 字符串中获取第一个非空的值。
   *
   * @param cookie Cookie 字符串
   * @param name 一个或多个 Cookie 名称
   * @return 第一个可用的值，不存在时返回 null
   */
  public static String getAny(String cookie, String... name) {
    List<String> result = get(cookie, name);
    return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
  }

  /**
   * 按名称从 Cookie 字符串中获取值。
   *
   * @param cookie Cookie 字符串
   * @param name Cookie 名称
   * @return 对应的值，不存在时返回 null
   */
  public static String get(String cookie, String name) {
    Map<String, String> cookies = rawCookieToMap(cookie);
    return cookies.get(name);
  }

  /**
   * 从请求对象中获取指定名称的 Cookie。
   *
   * @param httpServletRequest HTTP 请求
   * @param name Cookie 名称
   * @return Cookie，不存在时返回 null
   */
  public static Cookie get(HttpServletRequest httpServletRequest, String name) {
    return WebUtils.getCookie(httpServletRequest, name);
  }

  /**
   * 从请求对象中获取指定名称 Cookie 的值。
   *
   * @param httpServletRequest HTTP 请求
   * @param name Cookie 名称
   * @return Cookie 值，不存在时返回 null
   */
  public static String getValue(HttpServletRequest httpServletRequest, String name) {
    Cookie cookie = get(httpServletRequest, name);
    return ObjectUtils.isNotEmpty(cookie) ? cookie.getValue() : null;
  }

  /**
   * 从请求的 Cookie 请求头中获取指定名称的值。
   *
   * @param httpServletRequest HTTP 请求
   * @param name Cookie 名称
   * @return Cookie 值，不存在时返回 null
   */
  public static String getFromHeader(HttpServletRequest httpServletRequest, String name) {
    String cookie = HeaderUtils.getCookie(httpServletRequest);
    return get(cookie, name);
  }

  /**
   * 从请求的 Cookie 请求头中获取指定名称的值。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @param name Cookie 名称
   * @return Cookie 值，不存在时返回 null
   */
  public static String getFromHeader(ServerHttpRequest serverHttpRequest, String name) {
    String cookie = HeaderUtils.getCookie(serverHttpRequest);
    return get(cookie, name);
  }

  /**
   * 从请求的 Cookie 请求头中获取指定名称的值。
   *
   * @param httpInputMessage HTTP 输入消息
   * @param name Cookie 名称
   * @return Cookie 值，不存在时返回 null
   */
  public static String getFromHeader(HttpInputMessage httpInputMessage, String name) {
    String cookie = HeaderUtils.getCookie(httpInputMessage);
    return get(cookie, name);
  }

  /**
   * 从请求的 Cookie 请求头中获取第一个非空的值。
   *
   * @param httpServletRequest HTTP 请求
   * @param name 一个或多个 Cookie 名称
   * @return 第一个可用的值，不存在时返回 null
   */
  public static String getAnyFromHeader(HttpServletRequest httpServletRequest, String... name) {
    String cookie = HeaderUtils.getCookie(httpServletRequest);
    return getAny(cookie, name);
  }

  /**
   * 从请求的 Cookie 请求头中获取第一个非空的值。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @param name 一个或多个 Cookie 名称
   * @return 第一个可用的值，不存在时返回 null
   */
  public static String getAnyFromHeader(ServerHttpRequest serverHttpRequest, String... name) {
    String cookie = HeaderUtils.getCookie(serverHttpRequest);
    return getAny(cookie, name);
  }

  /**
   * 从请求的 Cookie 请求头中获取第一个非空的值。
   *
   * @param httpInputMessage HTTP 输入消息
   * @param name 一个或多个 Cookie 名称
   * @return 第一个可用的值，不存在时返回 null
   */
  public static String getAnyFromHeader(HttpInputMessage httpInputMessage, String... name) {
    String cookie = HeaderUtils.getCookie(httpInputMessage);
    return getAny(cookie, name);
  }

  /**
   * 删除指定名称的 Cookie（通过设置过期时间为 0 实现）。
   *
   * @param response HTTP 响应
   * @param key Cookie 名称
   */
  public static void removeCookie(HttpServletResponse response, String key) {
    setCookie(response, key, null, 0);
  }

  /**
   * 写入指定名称和值的 Cookie，并设置路径、最大存活时间与 HttpOnly 属性。
   *
   * @param response HTTP 响应
   * @param name Cookie 名称
   * @param value Cookie 值
   * @param maxAgeInSeconds 最大存活时间（秒）
   */
  public static void setCookie(
      HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setMaxAge(maxAgeInSeconds);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
  }
}
