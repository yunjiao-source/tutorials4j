package tutorials4j.springcloud.gateway.demo1;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 染色过滤器
@Slf4j
@Component
public class TrafficColoringGlobalFilter implements GlobalFilter, Ordered {

  public static final String GRAY_TAG = "grayTag";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String headerGray = request.getHeaders().getFirst("X-Gray-Tag");
    String userId = request.getHeaders().getFirst("X-User-Id");

    String grayTag = headerGray;
    if (grayTag == null || grayTag.isBlank()) {
      grayTag = hitGrayByUserId(userId, 10) ? "gray" : "stable";
    }

    exchange.getAttributes().put(GRAY_TAG, grayTag);

    ServerHttpRequest mutatedRequest = request.mutate().header("X-Gray-Tag", grayTag).build();

    return chain.filter(exchange.mutate().request(mutatedRequest).build());
  }

  private boolean hitGrayByUserId(String userId, int grayPercent) {
    if (userId == null || userId.isBlank()) {
      return false;
    }
    CRC32 crc32 = new CRC32();
    crc32.update(userId.getBytes(StandardCharsets.UTF_8));
    long bucket = crc32.getValue() % 100;
    return bucket < grayPercent;
  }

  @Override
  public int getOrder() {
    return -20;
  }
}
