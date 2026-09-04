package tutorials4j.framework.auth.core.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import tutorials4j.framework.auth.core.annotation.ApiKeyRateLimiter;
import tutorials4j.framework.auth.core.common.OAuthErrorCode;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.spring.util.HeaderUtils;
import tutorials4j.framework.web.core.util.WebUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ApiKeyRateLimiterInterceptor implements HandlerInterceptor {
  private final ApiKeyRateLimiterService apiKeyRateLimiterService;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (log.isDebugEnabled()) {
      log.debug("API KEY 限制处理, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    ApiKeyRateLimiter apiKeyRateLimiter =
        WebUtils.getHandlerMethodAnnotation(handler, ApiKeyRateLimiter.class);
    if (ObjectUtils.isNotEmpty(apiKeyRateLimiter)) {
      String apiKeyValue = extractApiKey(request);
      if (StringUtils.isBlank(apiKeyValue)) {
        throw OAuthErrorCode.API_KEY_VALUE_NOT_EXIST.throwed();
      }

      if (!apiKeyRateLimiterService.allowMinuteRequest(apiKeyRateLimiter.name(), apiKeyValue)) {
        throw BaseErrorCode.TOO_MANY_REQUESTS.throwed("API Key 每分钟请求次数超限");
      }

      if (!apiKeyRateLimiterService.allowDailyRequest(apiKeyRateLimiter.name(), apiKeyValue)) {
        throw BaseErrorCode.TOO_MANY_REQUESTS.throwed("API Key 每日请求次数超限");
      }

      response.setHeader(DefaultConsts.HTTP_HEADER_API_KEY_LIMIT, "Pass");
    }

    return true;
  }

  private String extractApiKey(HttpServletRequest request) {
    // 方式1：?apikey=sk-xxx（GET 参数）
    String apiKey = request.getParameter("apikey");
    if (apiKey != null && !apiKey.isEmpty()) return apiKey;

    // 方式2：Authorization: Bearer sk-xxx 或 Authorization: sk-xxx
    String auth = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_AUTHORIZATION);
    if (auth != null && !auth.isEmpty()) {
      if (auth.startsWith("Bearer ")) {
        return auth.substring(7); // 去掉 "Bearer " 前缀
      }
      return auth;
    }

    // 方式3：apikey: sk-xxx（自定义 Header）
    apiKey = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_API_KEY);
    if (apiKey != null && !apiKey.isEmpty()) return apiKey;

    return null;
  }
}
