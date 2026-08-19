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
/**
 * 全局访问日志过滤器：为每个请求生成或透传 traceId，记录请求开始时间，并在请求完成或异常时输出访问日志。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

  /** 链路追踪 ID 在 exchange 属性与 MDC 中的键名。 */
  public static final String TRACE_ID = "traceId";

  /** 请求开始时间戳在 exchange 属性中的键名。 */
  public static final String START_TIME = "startTime";

  /**
   * 生成或透传 traceId 并写入请求头与 MDC，随后执行过滤链，在请求结束或异常时记录访问日志。
   *
   * @param exchange 当前请求的 {@link ServerWebExchange}
   * @param chain 网关过滤器链
   * @return 过滤链执行结果的 {@link Mono}
   */
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

  /**
   * 输出访问日志：包含 traceId、客户端 IP、请求方法、路径、状态码、耗时、路由与异常信息。
   *
   * @param exchange 当前请求的 {@link ServerWebExchange}
   * @param clientIp 客户端 IP 地址
   * @param ex 请求处理过程中出现的异常，无异常时为 null
   */
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

  /** 返回过滤器的执行顺序，取值为 -900，使其优先于大多数过滤器执行。 */
  @Override
  public int getOrder() {
    return -900;
  }
}
