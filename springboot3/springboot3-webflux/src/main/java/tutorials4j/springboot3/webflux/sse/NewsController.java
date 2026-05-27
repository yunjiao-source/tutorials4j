package tutorials4j.springboot3.webflux.sse;

import java.time.Duration;
import java.time.LocalTime;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Spring WebFlux 响应式方案
 *
 * @author yangyunjiao
 */
@RestController
public class NewsController {

  @GetMapping(path = "/news/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> streamNews() {
    // Flux.interval：创建一个每秒触发一次的定时器
    return Flux.interval(Duration.ofSeconds(1))
        .map(
            sequence ->
                ServerSentEvent.<String>builder()
                    .id(String.valueOf(sequence)) // 事件ID，可用于断线重连
                    .event("news-update") // 事件类型，前端可监听不同类型
                    .data("Latest update at: " + LocalTime.now()) // 真正的业务数据
                    .build());
  }
}
