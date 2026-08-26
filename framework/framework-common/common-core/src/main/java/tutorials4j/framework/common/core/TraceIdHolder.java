package tutorials4j.framework.common.core;

import static tutorials4j.framework.common.core.DefaultConsts.HTTP_HEADER_TRACE_ID;
import static tutorials4j.framework.common.core.DefaultConsts.HTTP_HEADER_TRACE_PARENT_SPAN_ID;
import static tutorials4j.framework.common.core.DefaultConsts.HTTP_HEADER_TRACE_SPAN_ID;

import org.slf4j.MDC;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class TraceIdHolder {

  private TraceIdHolder() {}

  public static void setTraceId(String traceId) {
    MDC.put(HTTP_HEADER_TRACE_ID, traceId);
  }

  public static void setSpanId(String spanId) {
    MDC.put(HTTP_HEADER_TRACE_SPAN_ID, spanId);
  }

  public static void setParentSpanId(String parentSpanId) {
    MDC.put(HTTP_HEADER_TRACE_PARENT_SPAN_ID, parentSpanId);
  }

  public static String currentTrace() {
    return MDC.get(HTTP_HEADER_TRACE_SPAN_ID);
  }

  public static String currentSpan() {
    return MDC.get(HTTP_HEADER_TRACE_SPAN_ID);
  }

  public static String currentParentSpan() {
    return MDC.get(HTTP_HEADER_TRACE_PARENT_SPAN_ID);
  }

  public static void clear() {
    MDC.remove(HTTP_HEADER_TRACE_ID);
    MDC.remove(HTTP_HEADER_TRACE_SPAN_ID);
    MDC.remove(HTTP_HEADER_TRACE_PARENT_SPAN_ID);
  }
}
