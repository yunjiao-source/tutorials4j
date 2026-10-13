package tutorials4j.toolkit.webmvc.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * HTTP 请求头工具类，提供从各种请求抽象（HttpServletRequest、ServerHttpRequest、HttpInputMessage、HttpHeaders）中
 * 读取请求头、Session ID、租户 ID、Cookie、Authorization 及 Bearer Token 的便捷静态方法。
 *
 * @author YunJiao Yang
 */
public interface HeaderUtils {

  static String getUserAgent(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, "User-Agent");
  }

  /**
   * 从 HttpHeaders 中获取指定名称的全部请求头值。
   *
   * @param httpHeaders HTTP 头集合
   * @param name 请求头名称
   * @return 值列表，不存在时返回 null
   */
  static List<String> getHeaders(HttpHeaders httpHeaders, String name) {
    return httpHeaders.get(name);
  }

  /**
   * 从 ServerHttpRequest 中获取指定名称的全部请求头值。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @param name 请求头名称
   * @return 值列表，不存在时返回 null
   */
  static List<String> getHeaders(ServerHttpRequest serverHttpRequest, String name) {
    return getHeaders(serverHttpRequest.getHeaders(), name);
  }

  /**
   * 从 HttpHeaders 中获取指定名称的第一个请求头值。
   *
   * @param httpHeaders HTTP 头集合
   * @param name 请求头名称
   * @return 第一个请求头值，不存在时返回 null
   */
  static String getHeader(HttpHeaders httpHeaders, String name) {
    List<String> values = getHeaders(httpHeaders, name);
    return CollectionUtils.isEmpty(values) ? null : values.get(0);
  }

  /**
   * 从 ServerHttpRequest 中获取指定名称的第一个请求头值。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @param name 请求头名称
   * @return 第一个请求头值，不存在时返回 null
   */
  static String getHeader(ServerHttpRequest serverHttpRequest, String name) {
    return getHeader(serverHttpRequest.getHeaders(), name);
  }

  /**
   * 从 HttpServletRequest 中获取指定名称的请求头值。
   *
   * @param httpServletRequest HTTP 请求
   * @param name 请求头名称
   * @return 请求头值，不存在时返回 null
   */
  static String getHeader(HttpServletRequest httpServletRequest, String name) {
    return httpServletRequest.getHeader(name);
  }

  /**
   * 判断 HttpHeaders 中是否包含指定名称的请求头。
   *
   * @param httpHeaders HTTP 头集合
   * @param name 请求头名称
   * @return 包含返回 true
   */
  static boolean hasHeader(HttpHeaders httpHeaders, String name) {
    return httpHeaders.containsHeader(name);
  }

  /**
   * 判断请求中指定名称的请求头是否非空。
   *
   * @param httpServletRequest HTTP 请求
   * @param name 请求头名称
   * @return 请求头值非空返回 true
   */
  static Boolean hasHeader(HttpServletRequest httpServletRequest, String name) {
    return StringUtils.isNotBlank(getHeader(httpServletRequest, name));
  }

  /**
   * 判断 ServerHttpRequest 中是否包含指定名称的请求头。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @param name 请求头名称
   * @return 包含返回 true
   */
  static Boolean hasHeader(ServerHttpRequest serverHttpRequest, String name) {
    return hasHeader(serverHttpRequest.getHeaders(), name);
  }

  /**
   * 获取请求中的 Cookie 请求头值。
   *
   * @param httpServletRequest HTTP 请求
   * @return Cookie 请求头值，不存在时返回 null
   */
  static String getCookie(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.COOKIE);
  }

  /**
   * 获取请求中的 Cookie 请求头值。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @return Cookie 请求头值，不存在时返回 null
   */
  static String getCookie(ServerHttpRequest serverHttpRequest) {
    return getHeader(serverHttpRequest, HttpHeaders.COOKIE);
  }

  /**
   * 获取请求中的 Cookie 请求头值。
   *
   * @param httpInputMessage HTTP 输入消息
   * @return Cookie 请求头值，不存在时返回 null
   */
  static String getCookie(HttpInputMessage httpInputMessage) {
    return getHeader(httpInputMessage.getHeaders(), HttpHeaders.COOKIE);
  }

  /**
   * 获取请求中的 Authorization 请求头值。
   *
   * @param httpServletRequest HTTP 请求
   * @return Authorization 请求头值，不存在时返回 null
   */
  static String getAuthorization(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.AUTHORIZATION);
  }

  /**
   * 从 Authorization 请求头中提取 Bearer Token。
   *
   * @param request HTTP 请求
   * @return Bearer Token，请求头不存在或格式不符时返回 null
   */
  static String getBearerToken(HttpServletRequest request) {
    String header = getAuthorization(request);
    if (StringUtils.isNotBlank(header)
        && Strings.CS.startsWith(header, DefaultConsts.BEARER_TOKEN)) {
      return Strings.CS.remove(header, DefaultConsts.BEARER_TOKEN);
    } else {
      return null;
    }
  }

  /**
   * 获取请求中的 Origin 请求头值。
   *
   * @param httpServletRequest HTTP 请求
   * @return Origin 请求头值，不存在时返回 null
   */
  static String getOrigin(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.ORIGIN);
  }
}
