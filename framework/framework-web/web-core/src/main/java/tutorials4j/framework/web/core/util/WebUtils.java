package tutorials4j.framework.web.core.util;

import cn.hutool.core.util.IdUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

/**
 * Web 工具类，提供链路追踪标识生成与处理器方法注解获取等辅助方法。
 *
 * @author Yun Jiao
 */
@Slf4j
public class WebUtils {
  /**
   * 生成链路追踪 ID（Trace ID）。
   *
   * @return 简易 UUID 形式的追踪 ID
   */
  public static String generateTraceId() {
    return IdUtil.fastSimpleUUID();
  }

  /**
   * 生成链路片段 ID（Span ID），取简易 UUID 前 8 位。
   *
   * @return 8 位片段 ID
   */
  public static String generateSpanId() {
    return IdUtil.fastSimpleUUID().substring(0, 8);
  }

  /**
   * 获取处理器方法上指定类型的注解。
   *
   * @param <T> 注解类型
   * @param handler 处理器对象
   * @param clazz 注解类型
   * @return 方法上的注解，若处理器非 HandlerMethod 或无对应注解则返回 {@code null}
   */
  public static <T extends Annotation> T getHandlerMethodAnnotation(
      Object handler, Class<T> clazz) {
    Method method = null;
    if (handler instanceof HandlerMethod handlerMethod) {
      method = handlerMethod.getMethod();
    }

    if (method == null) {
      return null;
    }

    return method.getAnnotation(clazz);
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
