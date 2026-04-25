package tutorials4j.framework.web.client.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.core.util.HttpRequestUtils;

import java.io.IOException;

/**
 * 请求头，响应头日志
 *
 * @author Yun Jiao
 */
@Slf4j
public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestUtils.requestLogger(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        HttpRequestUtils.responseLogger(response);
        return response;
    }
}
