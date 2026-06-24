package tutorials4j.springcloud.gateway.demo1;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//  trace 与访问日志
@Slf4j
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

  public static final String TRACE_ID = "traceId";
  public static final String START_TIME = "startTime";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String traceId =
        Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Trace-Id"))
            .filter(v -> !v.isBlank())
            .orElseGet(() -> UUID.randomUUID().toString().replace("-", ""));

    exchange.getAttributes().put(TRACE_ID, traceId);
    exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

    ServerHttpRequest request =
        exchange.getRequest().mutate().header("X-Trace-Id", traceId).build();

    InetSocketAddress remoteAddress = request.getRemoteAddress();
    String clientIp =
        remoteAddress == null ? "unknown" : remoteAddress.getAddress().getHostAddress();

    MDC.put(TRACE_ID, traceId);
    return chain
        .filter(exchange.mutate().request(request).build())
        .doOnSuccess(unused -> logAccess(exchange, clientIp, null))
        .doOnError(ex -> logAccess(exchange, clientIp, ex))
        .doFinally(signalType -> MDC.clear());
  }

  private void logAccess(ServerWebExchange exchange, String clientIp, Throwable ex) {
    long startTime = exchange.getAttributeOrDefault(START_TIME, System.currentTimeMillis());
    long cost = System.currentTimeMillis() - startTime;
    Integer status =
        exchange.getResponse().getStatusCode() == null
            ? 500
            : exchange.getResponse().getStatusCode().value();
    String routeId =
        String.valueOf(
            exchange
                .getAttributes()
                .getOrDefault(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, "unknown"));

    log.info(
        "access traceId={} clientIp={} method={} path={} status={} costMs={} route={} error={}",
        exchange.getAttribute(TRACE_ID),
        clientIp,
        exchange.getRequest().getMethod(),
        exchange.getRequest().getURI().getRawPath(),
        status,
        cost,
        routeId,
        ex == null ? "none" : ex.getClass().getSimpleName());
  }

  @Override
  public int getOrder() {
    return -900;
  }
}
