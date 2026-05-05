package tutorials4j.framework.web.rest.mdc;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.rest.util.RestUtils;

import java.io.IOException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class TraceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. 获取或生成追踪ID
        String traceId = httpRequest.getHeader(DefaultConsts.HTTP_TRACE_ID);
        String spanId = httpRequest.getHeader(DefaultConsts.HTTP_TRACE_SPAN_ID);
        String parentSpanId = httpRequest.getHeader(DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID);

        if (traceId == null || traceId.isEmpty()) {
            traceId = RestUtils.generateTraceId();
        }
        if (spanId == null || spanId.isEmpty()) {
            spanId = RestUtils.generateSpanId();
        }

        // 2. 设置到MDC
        MDC.put(DefaultConsts.HTTP_TRACE_ID, traceId);
        MDC.put(DefaultConsts.HTTP_TRACE_SPAN_ID, spanId);
        if (parentSpanId != null) {
            MDC.put(DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID, parentSpanId);
        }

        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 跟踪信息过滤器：{}", httpRequest.getRequestURI());
        }

        try {
            // 3. 添加追踪ID到响应头
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(DefaultConsts.HTTP_TRACE_ID, traceId);
                httpResponse.setHeader(DefaultConsts.HTTP_TRACE_SPAN_ID, spanId);
            }

            chain.doFilter(request, response);
        } finally {
            // 4. 清理MDC
            MDC.clear();
        }
    }
}
