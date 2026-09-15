package tutorials4j.toolkit.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LoggingMessageFormattingStrategy implements MessageFormattingStrategy {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

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
        ZonedDateTime.now().format(FORMATTER), connectionId, elapsed, category, sql);
  }
}
