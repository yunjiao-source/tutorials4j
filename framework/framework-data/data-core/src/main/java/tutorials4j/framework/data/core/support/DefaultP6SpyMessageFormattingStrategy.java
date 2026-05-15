package tutorials4j.framework.data.core.support;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DefaultP6SpyMessageFormattingStrategy implements MessageFormattingStrategy {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern(DefaultConsts.DATE_TIME_FORMAT);

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
