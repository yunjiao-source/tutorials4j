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

/**
 * 全局过滤器，用于将请求头中的 JWT 解析后，把用户与租户信息转发给下游服务。
 *
 * <p>当请求携带 {@code Authorization: Bearer <token>} 头时，解码 JWT 并将 {@code subject} 写入 {@code X-User-Id}
 * 头、将 {@code tenant_id} 声明写入 {@code X-Tenant-Id} 头后继续转发；请求头缺失或不是 Bearer 类型时直接放行。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class JwtRelayFilter implements GlobalFilter, Ordered {

  private final ReactiveJwtDecoder jwtDecoder;

  /**
   * 解码请求中的 JWT，并将用户与租户信息附加到请求头后继续转发。
   *
   * @param exchange 当前的服务器交换对象
   * @param chain 网关过滤器链
   * @return 过滤器链执行结果
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      return chain.filter(exchange);
    }

    String token = auth.substring(7);
    return jwtDecoder.decode(token).flatMap(jwt -> chain.filter(mutate(exchange, jwt)));
  }

  /**
   * 构建携带用户与租户信息请求头的新交换对象。
   *
   * @param exchange 原始交换对象
   * @param jwt 已解码的 JWT
   * @return 携带 {@code X-User-Id} 与 {@code X-Tenant-Id} 头的新交换对象
   */
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

  /**
   * 返回过滤器的执行顺序，优先于其他过滤器执行。
   *
   * @return 顺序值 -100
   */
  @Override
  public int getOrder() {
    return -100;
  }
}
