package tutorials4j.springcloud.gateway.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtRelayFilter implements GlobalFilter, Ordered {

  private final ReactiveJwtDecoder jwtDecoder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      return chain.filter(exchange);
    }

    String token = auth.substring(7);
    return jwtDecoder.decode(token).flatMap(jwt -> chain.filter(mutate(exchange, jwt)));
  }

  private ServerWebExchange mutate(ServerWebExchange exchange, Jwt jwt) {
    return exchange
        .mutate()
        .request(
            builder ->
                builder
                    .header("X-User-Id", jwt.getSubject())
                    .header("X-Tenant-Id", String.valueOf(jwt.getClaim("tenant_id"))))
        .build();
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
