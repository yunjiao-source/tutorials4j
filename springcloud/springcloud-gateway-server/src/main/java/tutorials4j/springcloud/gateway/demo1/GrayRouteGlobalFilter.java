package tutorials4j.springcloud.gateway.demo1;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 灰度路由过滤器

@Slf4j
@Component
@RequiredArgsConstructor
public class GrayRouteGlobalFilter implements GlobalFilter, Ordered {

  private final ReactiveDiscoveryClient discoveryClient;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String grayTag = exchange.getAttributeOrDefault("grayTag", "stable");
    Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
    if (route == null || route.getUri() == null || !"lb".equals(route.getUri().getScheme())) {
      return chain.filter(exchange);
    }

    String serviceId = route.getUri().getHost();
    String targetVersion = "gray".equals(grayTag) ? "v2" : "v1";

    return discoveryClient
        .getInstances(serviceId)
        .filter(instance -> targetVersion.equals(instance.getMetadata().get("version")))
        .collectList()
        .flatMap(
            instances -> {
              if (instances.isEmpty()) {
                log.warn(
                    "No instance matched targetVersion={}, serviceId={}, fallback to default",
                    targetVersion,
                    serviceId);
                return chain.filter(exchange);
              }

              ServiceInstance selected = choose(instances);
              URI requestUrl =
                  selected.getUri().resolve(exchange.getRequest().getURI().getRawPath());
              exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
              exchange.getAttributes().put("targetVersion", targetVersion);
              return chain.filter(exchange);
            });
  }

  private ServiceInstance choose(List<ServiceInstance> instances) {
    return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
  }

  @Override
  public int getOrder() {
    return 10050;
  }
}
