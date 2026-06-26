package tutorials4j.framework.web.flux;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import tutorials4j.framework.web.core.exception.WebErrorCode;

/**
 * 响应式客户端工具类
 *
 * @author Yun Jiao
 */
@Slf4j
public class ClientUtils {
  public static ExchangeFilterFunction ofAuth() {
    return (clientRequest, next) -> {
      return ReactiveSecurityContextHolder.getContext()
          .map(SecurityContext::getAuthentication)
          .flatMap(
              authentication -> {
                if (authentication != null && authentication.isAuthenticated()) {
                  Object credentials = authentication.getCredentials();
                  String token = null;
                  if (credentials instanceof String tokenStr) {
                    token = tokenStr;
                  } else if (credentials != null) {
                    token = credentials.toString();
                  }
                  if (StringUtils.isNotBlank(token)) {
                    if (log.isDebugEnabled()) {
                      log.debug(
                          "为请求添加 Authorization Token: Bearer {}...",
                          token.substring(0, Math.min(8, token.length())));
                    }
                    ClientRequest authenticatedRequest =
                        ClientRequest.from(clientRequest)
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return next.exchange(authenticatedRequest);
                  }
                }
                log.warn("无法从 SecurityContext 获取有效 Token，继续原请求");
                return next.exchange(clientRequest);
              })
          .switchIfEmpty(
              Mono.defer(
                  () -> {
                    log.warn("SecurityContext 为空，无法获取 Token");
                    return next.exchange(clientRequest);
                  }));
    };
  }

  public static ExchangeFilterFunction ofRetry(
      long maxAttempts, Duration minBackoff, Duration maxBackoff) {
    return (clientRequest, next) -> {
      final String method = clientRequest.method().name();
      final String url = clientRequest.url().toString();

      RetryBackoffSpec spec =
          Retry.backoff(maxAttempts, minBackoff)
              .maxBackoff(maxBackoff)
              .doAfterRetry(
                  rs ->
                      log.info(
                          "[Retry] Request {} {} Exception, maxAttempts={}, totalRetries={}, totalRetriesInARow={}, failure={}",
                          method,
                          url,
                          maxAttempts,
                          rs.totalRetries(),
                          rs.totalRetriesInARow(),
                          rs.failure() == null ? "" : rs.failure().toString()));
      return next.exchange(clientRequest).retryWhen(spec);
    };
  }

  /**
   * 创建一个用于捕获异常并转换为框架自定义异常的 {@link ExchangeFilterFunction}。
   *
   * <p>该过滤器会对响应状态码进行检查，如果状态码大于 300（即非成功状态码，2xx 除外）， 则读取响应体内容，并将其作为异常消息，抛出异常； 否则直接返回原始响应。
   *
   * <p>注意：由于响应体只能被消费一次，该过滤器会消耗响应体，后续的处理器将无法再次读取响应体。
   *
   * @return 用于异常捕获和转换的 {@link ExchangeFilterFunction} 实例
   */
  public static ExchangeFilterFunction ofCatchExcepitonLogger() {
    return ExchangeFilterFunction.ofResponseProcessor(
        response -> {
          HttpStatusCode status = response.statusCode();
          if (status.value() > 300) {
            return response
                .bodyToMono(String.class)
                .flatMap(body -> Mono.error(WebErrorCode.WEB_CLIENT_FAILURE.throwed(body)));
          }

          return Mono.just(response);
        });
  }

  /**
   * 创建一个用于记录客户端请求日志的 {@link ExchangeFilterFunction}。
   *
   * <p>该过滤器在请求发送前，调用 {@link #requestLoggerDetails(ClientRequest)} 方法， 将请求的方法、URL 以及所有请求头信息输出到日志中。
   *
   * @return 请求日志记录的 {@link ExchangeFilterFunction} 实例
   * @see #requestLoggerDetails(ClientRequest)
   */
  public static ExchangeFilterFunction ofClientRequestLogger() {
    return ExchangeFilterFunction.ofRequestProcessor(
        clientRequest -> {
          requestLoggerDetails(clientRequest);
          return Mono.just(clientRequest);
        });
  }

  /**
   * 创建一个用于记录客户端响应日志的 {@link ExchangeFilterFunction}。
   *
   * <p>该过滤器在收到响应后，调用 {@link #responseLoggerDetails(ClientResponse)} 方法， 将响应的状态码以及所有响应头信息输出到日志中。
   *
   * @return 响应日志记录的 {@link ExchangeFilterFunction} 实例
   * @see #responseLoggerDetails(ClientResponse)
   */
  public static ExchangeFilterFunction ofClientResponseLogger() {
    return ExchangeFilterFunction.ofResponseProcessor(
        clientResponse -> {
          responseLoggerDetails(clientResponse);
          return Mono.just(clientResponse);
        });
  }

  /**
   * 记录客户端响应的详细信息。
   *
   * <p>该方法会从 {@link ClientResponse} 中提取状态码和所有响应头， 拼接成格式化的字符串后通过日志输出（INFO 级别）。
   *
   * <p>输出格式示例：
   *
   * <pre>
   * [ClientResponse]响应: 200 OK
   * 响应头列表:
   * Content-Type=application/json
   * Content-Length=123
   * </pre>
   *
   * @param response 需要记录日志的客户端响应对象，不能为 {@code null}
   */
  public static void responseLoggerDetails(ClientResponse response) {
    StringBuilder sb = new StringBuilder("\n");
    sb.append("[ClientResponse]响应: ").append(response.statusCode()).append("\n");
    sb.append("响应头列表: \n");
    response.headers().asHttpHeaders().forEach((k, v) -> headerLogger(sb, k, v));
    log.info(sb.toString());
  }

  /**
   * 记录客户端请求的详细信息。
   *
   * <p>该方法会从 {@link ClientRequest} 中提取请求方法、URL 以及所有请求头， 拼接成格式化的字符串后通过日志输出（INFO 级别）。
   *
   * <p>输出格式示例：
   *
   * <pre>
   * [ClientRequest]请求: GET https://api.example.com/data
   * 请求头列表:
   * Accept=application/json
   * Authorization=Bearer xxxx
   * </pre>
   *
   * @param request 需要记录日志的客户端请求对象，不能为 {@code null}
   */
  public static void requestLoggerDetails(ClientRequest request) {
    StringBuilder sb = new StringBuilder("\n");
    sb.append("[ClientRequest]请求: ")
        .append(request.method())
        .append(" ")
        .append(request.url())
        .append("\n");
    sb.append("请求头列表: \n");
    request.headers().forEach((k, v) -> headerLogger(sb, k, v));
    log.info(sb.toString());
  }

  /**
   * 辅助方法：将单个请求或响应的头信息追加到日志字符串构建器中。
   *
   * <p>对于给定的头名称和对应的值列表，该方法会将每个值以“名称=值”的格式追加到 {@link StringBuilder} 中， 每个头值独占一行。
   *
   * @param logBuilder 用于拼接日志内容的 {@link StringBuilder} 实例
   * @param name 头名称
   * @param values 与头名称对应的值列表
   */
  private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
    values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
  }
}
