package tutorials4j.framework.common.spring.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.SymbolConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SessionUtils {

  public static HttpSession getSession(HttpServletRequest httpServletRequest, boolean create) {
    return httpServletRequest.getSession(create);
  }

  public static HttpSession getSession(HttpServletRequest httpServletRequest) {
    return getSession(httpServletRequest, false);
  }

  public static String getSessionId(HttpServletRequest httpServletRequest, boolean create) {
    HttpSession httpSession = getSession(httpServletRequest, create);
    return ObjectUtils.isNotEmpty(httpSession) ? httpSession.getId() : null;
  }

  public static String getSessionId(HttpServletRequest httpServletRequest) {
    return getSessionId(httpServletRequest, false);
  }

  public static String getSessionIdFromHeader(HttpInputMessage httpInputMessage) {
    return CookieUtils.getAnyFromHeader(httpInputMessage, DefaultConsts.SESSION_IDS);
  }

  public static String getSessionIdFromHeader(ServerHttpRequest serverHttpRequest) {
    return CookieUtils.getAnyFromHeader(serverHttpRequest, DefaultConsts.SESSION_IDS);
  }

  public static String analyseSessionId(HttpServletRequest httpServletRequest) {
    String sessionId = getSessionId(httpServletRequest);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(httpServletRequest);
    }
    return sessionId;
  }

  public static String analyseSessionId(ServerHttpRequest serverHttpRequest) {
    String sessionId = getSessionIdFromHeader(serverHttpRequest);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(serverHttpRequest);
    }
    return sessionId;
  }

  public static String analyseSessionId(HttpInputMessage httpInputMessage) {
    String sessionId = getSessionIdFromHeader(httpInputMessage);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(httpInputMessage);
    }
    return sessionId;
  }

  public static boolean isCryptoEnabled(HttpServletRequest httpServletRequest, String sessionId) {
    return HeaderUtils.hasSessionIdHeader(httpServletRequest) && StringUtils.isNotBlank(sessionId);
  }

  public static boolean isCryptoEnabled(HttpInputMessage httpInputMessage, String sessionId) {
    return HeaderUtils.hasSessionIdHeader(httpInputMessage) && StringUtils.isNotBlank(sessionId);
  }

  public static String generateRequestKey(HttpServletRequest request) {
    String sessionId = analyseSessionId(request);
    String url = request.getRequestURI();
    String method = request.getMethod();

    if (StringUtils.isNotBlank(sessionId)) {
      return sessionId + SymbolConsts.COLON + url + SymbolConsts.COLON + method;
    } else {
      return url + SymbolConsts.COLON + method;
    }
  }
}
