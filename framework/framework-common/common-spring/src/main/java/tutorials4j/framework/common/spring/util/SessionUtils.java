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
 * Session 工具类，提供获取 HttpSession、Session ID（从会话、请求头、Cookie）以及生成请求密钥等能力， 用于会话与加密相关的处理。
 *
 * @author Yun Jiao
 */
public class SessionUtils {

  /**
   * 按需创建并获取请求关联的 HttpSession。
   *
   * @param httpServletRequest HTTP 请求
   * @param create 会话不存在时是否创建新会话
   * @return HttpSession
   */
  public static HttpSession getSession(HttpServletRequest httpServletRequest, boolean create) {
    return httpServletRequest.getSession(create);
  }

  /**
   * 获取请求关联的 HttpSession（不存在时不创建）。
   *
   * @param httpServletRequest HTTP 请求
   * @return HttpSession，不存在时返回 null
   */
  public static HttpSession getSession(HttpServletRequest httpServletRequest) {
    return getSession(httpServletRequest, false);
  }

  /**
   * 获取会话 ID。
   *
   * @param httpServletRequest HTTP 请求
   * @param create 会话不存在时是否创建新会话
   * @return 会话 ID，无会话时返回 null
   */
  public static String getSessionId(HttpServletRequest httpServletRequest, boolean create) {
    HttpSession httpSession = getSession(httpServletRequest, create);
    return ObjectUtils.isNotEmpty(httpSession) ? httpSession.getId() : null;
  }

  /**
   * 获取会话 ID（不创建会话）。
   *
   * @param httpServletRequest HTTP 请求
   * @return 会话 ID，无会话时返回 null
   */
  public static String getSessionId(HttpServletRequest httpServletRequest) {
    return getSessionId(httpServletRequest, false);
  }

  /**
   * 从请求 Cookie 头中获取 Session ID。
   *
   * @param httpInputMessage HTTP 输入消息
   * @return Session ID，不存在时返回 null
   */
  public static String getSessionIdFromHeader(HttpInputMessage httpInputMessage) {
    return CookieUtils.getAnyFromHeader(httpInputMessage, DefaultConsts.SESSION_IDS);
  }

  /**
   * 从请求 Cookie 头中获取 Session ID。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @return Session ID，不存在时返回 null
   */
  public static String getSessionIdFromHeader(ServerHttpRequest serverHttpRequest) {
    return CookieUtils.getAnyFromHeader(serverHttpRequest, DefaultConsts.SESSION_IDS);
  }

  /**
   * 分析并获取会话 ID：优先取 HttpSession 的 ID，为空时回退到请求头中的 Session ID。
   *
   * @param httpServletRequest HTTP 请求
   * @return 会话 ID
   */
  public static String analyseSessionId(HttpServletRequest httpServletRequest) {
    String sessionId = getSessionId(httpServletRequest);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(httpServletRequest);
    }
    return sessionId;
  }

  /**
   * 分析并获取会话 ID：优先取请求头 Cookie 中的 Session ID，为空时回退到 Session ID 请求头。
   *
   * @param serverHttpRequest 服务端 HTTP 请求
   * @return 会话 ID
   */
  public static String analyseSessionId(ServerHttpRequest serverHttpRequest) {
    String sessionId = getSessionIdFromHeader(serverHttpRequest);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(serverHttpRequest);
    }
    return sessionId;
  }

  /**
   * 分析并获取会话 ID：优先取请求头 Cookie 中的 Session ID，为空时回退到 Session ID 请求头。
   *
   * @param httpInputMessage HTTP 输入消息
   * @return 会话 ID
   */
  public static String analyseSessionId(HttpInputMessage httpInputMessage) {
    String sessionId = getSessionIdFromHeader(httpInputMessage);
    if (StringUtils.isBlank(sessionId)) {
      sessionId = HeaderUtils.getSessionId(httpInputMessage);
    }
    return sessionId;
  }

  /**
   * 判断是否启用会话加密：需携带 Session ID 请求头且会话 ID 非空。
   *
   * @param httpServletRequest HTTP 请求
   * @param sessionId 会话 ID
   * @return 是否启用加密
   */
  public static boolean isCryptoEnabled(HttpServletRequest httpServletRequest, String sessionId) {
    return HeaderUtils.hasSessionIdHeader(httpServletRequest) && StringUtils.isNotBlank(sessionId);
  }

  /**
   * 判断是否启用会话加密：需携带 Session ID 请求头且会话 ID 非空。
   *
   * @param httpInputMessage HTTP 输入消息
   * @param sessionId 会话 ID
   * @return 是否启用加密
   */
  public static boolean isCryptoEnabled(HttpInputMessage httpInputMessage, String sessionId) {
    return HeaderUtils.hasSessionIdHeader(httpInputMessage) && StringUtils.isNotBlank(sessionId);
  }

  /**
   * 根据会话 ID、请求 URI 与请求方法生成请求密钥（用于会话加密的请求绑定）。
   *
   * @param request HTTP 请求
   * @return 请求密钥
   */
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
