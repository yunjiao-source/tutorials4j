package tutorials4j.springboot3.data.orm.cursor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 游标工具类
 *
 * @author Yun Jiao
 */
public class CursorUtils {
  private static final String SEP = "::";

  /**
   * 解析游标
   *
   * @param cursor 游标字符串，格式为 "id:timestamp"
   * @return 游标对象
   */
  public static Cursor parseCursor(String cursor) {
    if (cursor == null || cursor.isEmpty()) {
      return null;
    }

    try {
      String[] parts = cursor.split(SEP);
      if (parts.length != 2) {
        return null;
      }

      Long id = Long.parseLong(parts[0]);
      String timestampStr = parts[1];
      LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);

      return new Cursor(id, timestamp);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 生成游标
   *
   * @param id ID
   * @param timestamp 时间戳
   * @return 游标字符串
   */
  public static String generateCursor(Long id, LocalDateTime timestamp) {
    if (id == null || timestamp == null) {
      return null;
    }

    return id + SEP + timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
  }

  @Data
  @AllArgsConstructor
  public static class Cursor {

    private Long id;
    private LocalDateTime timestamp;
  }
}
