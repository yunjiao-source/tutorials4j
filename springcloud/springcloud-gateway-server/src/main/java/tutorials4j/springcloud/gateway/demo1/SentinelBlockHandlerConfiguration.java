package tutorials4j.springcloud.gateway.demo1;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import jakarta.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SentinelBlockHandlerConfiguration {

  @PostConstruct
  public void init() {
    BlockRequestHandler blockHandler =
        (exchange, ex) -> {
          Map<String, Object> body = new LinkedHashMap<>();
          body.put("code", 429);
          body.put("message", "请求过于频繁，请稍后重试");
          body.put("routeId", exchange.getAttributeOrDefault("routeId", "unknown"));
          body.put("traceId", exchange.getAttributeOrDefault("traceId", "unknown"));
          body.put("timestamp", System.currentTimeMillis());

          return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(body);
        };
    GatewayCallbackManager.setBlockHandler(blockHandler);
  }
}
