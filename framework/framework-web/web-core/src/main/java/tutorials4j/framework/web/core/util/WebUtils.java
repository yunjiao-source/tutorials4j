package tutorials4j.framework.web.core.util;

import cn.hutool.core.util.IdUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应式客户端工具类
 *
 * @author Yun Jiao
 */
@Slf4j
public class WebUtils {
  public static String generateTraceId() {
    return IdUtil.fastSimpleUUID();
  }

  public static String generateSpanId() {
    return IdUtil.fastSimpleUUID().substring(0, 8);
  }

  /**
   * 辅助方法：将单个请求或响应的头信息追加到日志字符串构建器中。
   *
   * <p>对于给定的头名称和对应的值列表，该方法会将每个值以“名称=值”的格式追加到 {@link StringBuilder} 中， 每个头值独占一行。
   *
   * @param logBuilder 用于拼接日志内容的 {@link StringBuilder} 实例
   * @param name 头名称
   * @param values 与头名称对应的值列表
   */
  private static void headerLogger(StringBuilder logBuilder, String name, List<String> values) {
    values.forEach(value -> logBuilder.append(name).append("=").append(value).append("\n"));
  }
}
