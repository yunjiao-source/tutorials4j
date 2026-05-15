package tutorials4j.springboot3.mdc;

import java.util.UUID;

/**
 * 追踪ID生成器
 *
 * @author Yun Jiao
 */
public interface TraceIdGenerator {
  static String generateTraceId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  static String generateSpanId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
