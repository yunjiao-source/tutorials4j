package tutorials4j.framework.web.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

/**
 * 请求头，响应头日志
 *
 * @author Yun Jiao
 */
@Slf4j
public class LogHeaderClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 记录请求日志
        log.info("请求: {} {}", request.getMethod(), request.getURI());
        log.info("--- 请求头列表: ---");
        request.getHeaders().forEach(this::logHeader);

        ClientHttpResponse response = execution.execute(request, body);

        // 记录响应日志
        log.info("响应: {}", response.getStatusCode());
        log.info("--- 响应头列表: ---");
        response.getHeaders().forEach(this::logHeader);
        return response;
    }

    private void logHeader(String name, List<String> values) {
        values.forEach(value -> log.info("{}={}", name, value));
    }
}
