package tutorials4j.framework.common.spring.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class HeaderUtils {

  public static List<String> getHeaders(HttpHeaders httpHeaders, String name) {
    return httpHeaders.get(name);
  }

  public static List<String> getHeaders(ServerHttpRequest serverHttpRequest, String name) {
    return getHeaders(serverHttpRequest.getHeaders(), name);
  }

  public static String getHeader(HttpHeaders httpHeaders, String name) {
    List<String> values = getHeaders(httpHeaders, name);
    return CollectionUtils.isNotEmpty(values) ? values.get(0) : null;
  }

  public static String getHeader(ServerHttpRequest serverHttpRequest, String name) {
    return getHeader(serverHttpRequest.getHeaders(), name);
  }

  public static String getHeader(HttpServletRequest httpServletRequest, String name) {
    return httpServletRequest.getHeader(name);
  }

  public static boolean hasHeader(HttpHeaders httpHeaders, String name) {
    return httpHeaders.containsKey(name);
  }

  public static Boolean hasHeader(HttpServletRequest httpServletRequest, String name) {
    return StringUtils.isNotBlank(getHeader(httpServletRequest, name));
  }

  public static Boolean hasHeader(ServerHttpRequest serverHttpRequest, String name) {
    return hasHeader(serverHttpRequest.getHeaders(), name);
  }

  public static String getSessionId(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static String getSessionId(ServerHttpRequest serverHttpRequest) {
    return getHeader(serverHttpRequest, DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static String getSessionId(HttpInputMessage httpInputMessage) {
    return getHeader(httpInputMessage.getHeaders(), DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static String getTenantId(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, DefaultConsts.HTTP_HEADER_TENANT);
  }

  public static boolean hasSessionIdHeader(HttpServletRequest httpServletRequest) {
    return hasHeader(httpServletRequest, DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static boolean hasSessionIdHeader(ServerHttpRequest serverHttpRequest) {
    return hasHeader(serverHttpRequest, DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static boolean hasSessionIdHeader(HttpInputMessage httpInputMessage) {
    return hasHeader(httpInputMessage.getHeaders(), DefaultConsts.HTTP_HEADER_SESSION_ID);
  }

  public static String getCookie(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.COOKIE);
  }

  public static String getCookie(ServerHttpRequest serverHttpRequest) {
    return getHeader(serverHttpRequest, HttpHeaders.COOKIE);
  }

  public static String getCookie(HttpInputMessage httpInputMessage) {
    return getHeader(httpInputMessage.getHeaders(), HttpHeaders.COOKIE);
  }

  public static String getAuthorization(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.AUTHORIZATION);
  }

  public static String getBearerToken(HttpServletRequest request) {
    String header = getAuthorization(request);
    if (StringUtils.isNotBlank(header)
        && StringUtils.startsWith(header, DefaultConsts.BEARER_TOKEN)) {
      return StringUtils.remove(header, DefaultConsts.BEARER_TOKEN);
    } else {
      return null;
    }
  }

  public static String getOrigin(HttpServletRequest httpServletRequest) {
    return getHeader(httpServletRequest, HttpHeaders.ORIGIN);
  }
}
