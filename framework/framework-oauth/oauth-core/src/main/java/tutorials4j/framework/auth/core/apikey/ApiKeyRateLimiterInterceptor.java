package tutorials4j.framework.auth.core.apikey;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.web.servlet.HandlerInterceptor;
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
      log.debug("API KEY 限流处理, method={}, uri={}", request.getMethod(), request.getRequestURI());
    }

    ApiKeyRateLimiter apiKeyRateLimiter =
        WebUtils.getHandlerMethodAnnotation(handler, ApiKeyRateLimiter.class);
    if (ObjectUtils.isNotEmpty(apiKeyRateLimiter)) {
      String apiKeyValue = extractApiKey(request);
      if (StringUtils.isBlank(apiKeyValue)) {
        throw OAuthErrorCode.API_KEY_VALUE_NOT_EXIST.throwed();
      }

      Triple<Long, Long, Duration> rateLimiter =
          apiKeyRateLimiterService.getRateLimiter(apiKeyRateLimiter.name(), apiKeyValue);
      if (rateLimiter.getLeft() > rateLimiter.getMiddle()) {
        throw BaseErrorCode.TOO_MANY_REQUESTS.throwed("API Key 请求次数超限");
      }

      response.setHeader(DefaultConsts.HTTP_HEADER_API_KEY_LIMIT_VERIFIED, "ok");
      response.setHeader(
          DefaultConsts.HTTP_HEADER_API_KEY_LIMIT, String.valueOf(rateLimiter.getMiddle()));
      response.setHeader(
          DefaultConsts.HTTP_HEADER_API_KEY_LIMIT_REMAINING,
          String.valueOf(rateLimiter.getMiddle() - rateLimiter.getLeft()));
      response.setHeader(
          DefaultConsts.HTTP_HEADER_API_KEY_LIMIT_TIME_WINDOW, rateLimiter.getRight().toString());
    }

    return true;
  }

  private String extractApiKey(HttpServletRequest request) {
    // 方式：?apikey=sk-xxx（GET 参数）
    String apiKey = request.getParameter(DefaultConsts.HTTP_QUERY_PARAM_API_KEY);
    if (apiKey != null && !apiKey.isEmpty()) return apiKey;

    // 方式：apikey: sk-xxx（自定义 Header）
    apiKey = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_API_KEY);
    if (apiKey != null && !apiKey.isEmpty()) return apiKey;

    // 方式：Authorization: Bearer sk-xxx 或 Authorization: sk-xxx
    String auth = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_AUTHORIZATION);
    if (auth != null && !auth.isEmpty()) {
      if (auth.startsWith(DefaultConsts.BEARER_TOKEN)) {
        return auth.substring(7); // 去掉 "Bearer " 前缀
      }
      return auth;
    }

    return null;
  }
}
