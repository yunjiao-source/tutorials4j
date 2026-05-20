package tutorials4j.framework.web.rest.mdc;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.rest.util.RestUtils;

/**
 * Servlet 过滤器，用于在 Web 请求处理入口处初始化和传播链路追踪标识。
 *
 * <p>该过滤器从 HTTP 请求头中读取 traceId、spanId 和 parentSpanId，若缺失则自动生成。 将追踪标识存入 MDC（Mapped Diagnostic
 * Context），以便后续日志输出和下游调用时使用。 同时将 traceId 和 spanId 写入 HTTP 响应头，便于客户端或网关获取调用链信息。 请求处理完成后，清理当前线程的
 * MDC，防止线程池复用导致上下文污染。
 *
 * @author Yun Jiao
 * @see org.slf4j.MDC
 */
@Slf4j
public class TraceRequestFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. 获取或生成追踪ID
    String traceId = request.getHeader(DefaultConsts.HTTP_HEADER_TRACE_ID);
    String spanId = request.getHeader(DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID);
    String parentSpanId = request.getHeader(DefaultConsts.HTTP_HEADER_TRACE_PARENT_SPAN_ID);

    if (traceId == null || traceId.isEmpty()) {
      traceId = RestUtils.generateTraceId();
    }
    if (spanId == null || spanId.isEmpty()) {
      spanId = RestUtils.generateSpanId();
    }

    // 2. 设置到MDC
    MDC.put(DefaultConsts.HTTP_HEADER_TRACE_ID, traceId);
    MDC.put(DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID, spanId);
    if (parentSpanId != null) {
      MDC.put(DefaultConsts.HTTP_HEADER_TRACE_PARENT_SPAN_ID, parentSpanId);
    }

    if (log.isDebugEnabled()) {
      log.debug("[WEB-REST] 跟踪信息过滤器：{}", request.getRequestURI());
    }

    try {
      // 3. 添加追踪ID到响应头
      if (response instanceof HttpServletResponse httpResponse) {
        httpResponse.setHeader(DefaultConsts.HTTP_HEADER_TRACE_ID, traceId);
        httpResponse.setHeader(DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID, spanId);
      }

      filterChain.doFilter(request, response);
    } finally {
      // 4. 清理MDC
      MDC.clear();
    }
  }
}
