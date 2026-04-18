package tutorials4j.springboot3.mdc;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * MDC 拦截器: 过滤器方式
 *
 * @author Yun Jiao
 */
@Component
@Order(1)
public class TraceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. 获取或生成追踪ID
        String traceId = httpRequest.getHeader(TraceConstants.TRACE_ID);
        String spanId = httpRequest.getHeader(TraceConstants.SPAN_ID);
        String parentSpanId = httpRequest.getHeader(TraceConstants.PARENT_SPAN_ID);

        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdGenerator.generateTraceId();
        }
        if (spanId == null || spanId.isEmpty()) {
            spanId = TraceIdGenerator.generateSpanId();
        }

        // 2. 设置到MDC
        MDC.put(TraceConstants.TRACE_ID, traceId);
        MDC.put(TraceConstants.SPAN_ID, spanId);
        if (parentSpanId != null) {
            MDC.put(TraceConstants.PARENT_SPAN_ID, parentSpanId);
        }

        try {
            // 3. 添加追踪ID到响应头（可选）
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(TraceConstants.TRACE_ID, traceId);
                httpResponse.setHeader(TraceConstants.SPAN_ID, spanId);
            }

            chain.doFilter(request, response);
        } finally {
            // 4. 清理MDC
            MDC.clear();
        }
    }
}