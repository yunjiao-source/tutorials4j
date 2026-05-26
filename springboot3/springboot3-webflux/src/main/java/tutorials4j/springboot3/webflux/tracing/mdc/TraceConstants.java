package tutorials4j.springboot3.webflux.tracing.mdc;

/**
 * 追踪常量定义
 *
 * @author Yun Jiao
 */
public interface TraceConstants {
  String TRACE_ID = "X-Trace-Id";
  String SPAN_ID = "X-Span-Id";
  String PARENT_SPAN_ID = "X-Parent-Span-Id";
}
