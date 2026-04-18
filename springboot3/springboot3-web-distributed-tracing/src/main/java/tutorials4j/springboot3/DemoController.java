package tutorials4j.springboot3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tutorials4j.springboot3.mdc.TraceConstants;

/**
 * 接口
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DemoController {
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final DemoAsyncService demoAsync;

    @GetMapping("/rest-template")
    public String restTemplate() {
        // 日志会自动包含traceId
        log.info("Processing request");

        // 调用其他服务，追踪信息会自动传递
        restTemplate.getForObject("https://www.baidu.com", String.class);

        log.info("Request completed");
        return "Trace ID: " + MDC.get(TraceConstants.TRACE_ID);
    }

    @GetMapping("/web-client")
    public String webClient() {
        // 日志会自动包含traceId
        log.info("Processing request");

        // 调用其他服务，追踪信息会自动传递
        webClient.get() // 指定请求方法为 GET
                .uri("https://www.baidu.com") // 指定请求地址
                .retrieve() // 获取响应（自动处理 2xx 状态码，非 2xx 会抛出异常）
                .bodyToMono(String.class) // 将响应体转换为 Mono<String>（响应式类型）
                .block();

        log.info("Request completed");
        return "Trace ID: " + MDC.get(TraceConstants.TRACE_ID);
    }

    @GetMapping("/async")
    public String async() {
        // 日志会自动包含traceId
        log.info("Processing request");

        demoAsync.async();

        log.info("Request completed");
        return "Trace ID: " + MDC.get(TraceConstants.TRACE_ID);
    }
}
