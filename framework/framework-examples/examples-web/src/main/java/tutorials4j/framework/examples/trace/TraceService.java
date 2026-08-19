package tutorials4j.framework.examples.trace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Trace（链路追踪）示例服务。
 *
 * <p>通过 {@link Async} 异步方法模拟跨线程调用第三方接口的场景， 用于验证链路追踪信息在异步线程中的传递。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TraceService {
  private final WebClient webClient;

  /** 异步执行：记录日志并通过 WebClient 调用第三方接口获取数据。 */
  @Async
  public void logger() {
    log.info("TraceService");
    // 调用第三方接口
    String path = "/posts/1";
    // getForObject(请求地址, 返回值类型)
    String result = webClient.get().uri(path).retrieve().bodyToMono(String.class).block();
    log.info("GET响应结果：{}", result);
  }
}
