package tutorials4j.springboot3.webflux.httpexchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class SecurityExchangeFilterFunction implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {
    // 从 Reactor 上下文中获取 SecurityContext
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(
            authentication -> {
              if (authentication != null && authentication.isAuthenticated()) {
                Object credentials = authentication.getCredentials();
                String token = null;
                if (credentials instanceof String) {
                  token = (String) credentials;
                } else if (credentials != null) {
                  token = credentials.toString();
                }
                if (token != null && !token.isEmpty()) {
                  log.debug(
                      "为请求添加 Authorization Token: Bearer {}",
                      token.substring(0, Math.min(8, token.length())) + "...");
                  ClientRequest authenticatedRequest =
                      ClientRequest.from(clientRequest)
                          .header("Authorization", "Bearer " + token)
                          .build();
                  return nextFilter.exchange(authenticatedRequest);
                }
              }
              log.warn("无法从 SecurityContext 获取有效 Token，继续原请求");
              return nextFilter.exchange(clientRequest);
            })
        .switchIfEmpty(
            Mono.defer(
                () -> {
                  log.warn("SecurityContext 为空，无法获取 Token");
                  return nextFilter.exchange(clientRequest);
                }));
  }
}
