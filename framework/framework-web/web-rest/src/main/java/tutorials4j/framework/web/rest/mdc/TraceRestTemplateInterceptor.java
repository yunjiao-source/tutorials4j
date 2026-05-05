package tutorials4j.framework.web.rest.mdc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.rest.util.RestUtils;

import java.io.IOException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class TraceRestTemplateInterceptor  implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        // 传递追踪信息到下游服务
        String traceId = MDC.get(DefaultConsts.HTTP_TRACE_ID);
        String spanId = MDC.get(DefaultConsts.HTTP_TRACE_SPAN_ID);

        if (traceId != null) {
            request.getHeaders().add(DefaultConsts.HTTP_TRACE_ID, traceId);
        }
        if (spanId != null) {
            // 生成新的子span
            String childSpanId = RestUtils.generateSpanId();
            request.getHeaders().add(DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID, spanId);
            request.getHeaders().add(DefaultConsts.HTTP_TRACE_SPAN_ID, childSpanId);
        }

        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 跟踪信息拦截器：{}", request.getURI());
        }

        return execution.execute(request, body);
    }
}
