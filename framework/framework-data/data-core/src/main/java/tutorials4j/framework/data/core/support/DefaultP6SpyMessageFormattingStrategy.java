package tutorials4j.framework.data.core.support;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * 默认 P6Spy SQL 日志格式化策略。
 *
 * <p>将 P6Spy 捕获的 SQL 执行信息格式化为统一的日志文本，包含执行时间、连接 ID、 耗时、日志类别与 SQL 语句，便于开发阶段排查 SQL 问题。
 *
 * @author Yun Jiao
 */
public class DefaultP6SpyMessageFormattingStrategy implements MessageFormattingStrategy {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern(DefaultConsts.DATE_TIME_FORMAT);

  /**
   * 格式化 SQL 执行日志消息。
   *
   * @param connectionId 连接 ID
   * @param now 当前时间字符串（P6Spy 传入，本实现未直接使用）
   * @param elapsed 执行耗时（毫秒）
   * @param category 日志类别
   * @param prepared 预编译 SQL（本实现未直接使用）
   * @param sql 实际执行的 SQL 语句
   * @param url 数据源 URL（本实现未直接使用）
   * @return 格式化后的日志文本
   */
  @Override
  public String formatMessage(
      int connectionId,
      String now,
      long elapsed,
      String category,
      String prepared,
      String sql,
      String url) {
    return String.format(
        "[%s] | 连接ID: %d | 耗时: %sms | %s | SQL: %s",
        LocalDateTime.now().format(FORMATTER), connectionId, elapsed, category, sql);
  }
}
