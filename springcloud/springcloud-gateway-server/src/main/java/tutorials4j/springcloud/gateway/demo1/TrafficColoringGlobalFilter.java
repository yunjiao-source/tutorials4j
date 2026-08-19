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
/**
 * 流量染色全局过滤器：优先采用请求头中的灰度标签，否则按用户 ID 哈希概率打标，并将灰度标签写入 exchange 属性与请求头透传至下游。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class TrafficColoringGlobalFilter implements GlobalFilter, Ordered {

  /** 灰度标签在 exchange 属性中的键名。 */
  public static final String GRAY_TAG = "grayTag";

  /**
   * 计算并写入灰度标签，同时将标签注入请求头后继续执行过滤链。
   *
   * @param exchange 当前请求的 {@link ServerWebExchange}
   * @param chain 网关过滤器链
   * @return 过滤链执行结果的 {@link Mono}
   */
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

  /**
   * 按用户 ID 的 CRC32 哈希值判断是否命中灰度，命中概率由 grayPercent 控制。
   *
   * @param userId 用户 ID
   * @param grayPercent 灰度命中百分比（0-100）
   * @return 命中灰度返回 true，否则返回 false
   */
  private boolean hitGrayByUserId(String userId, int grayPercent) {
    if (userId == null || userId.isBlank()) {
      return false;
    }
    CRC32 crc32 = new CRC32();
    crc32.update(userId.getBytes(StandardCharsets.UTF_8));
    long bucket = crc32.getValue() % 100;
    return bucket < grayPercent;
  }

  /** 返回过滤器的执行顺序，取值为 -20。 */
  @Override
  public int getOrder() {
    return -20;
  }
}
