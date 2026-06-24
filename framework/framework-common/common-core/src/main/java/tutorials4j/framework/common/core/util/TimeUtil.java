package tutorials4j.framework.common.core.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 企业级通用时间工具类 【规范配套】统一使用 Asia/Shanghai 时区 全程基于 Java 8+ java.time 包，线程安全 禁止使用 Date / SimpleDateFormat
 *
 * @author 项目架构组
 */
public final class TimeUtil {

  // 全局常量（强制统一）
  public static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");
  public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_PATTERN = "yyyy-MM-dd";
  public static final String TIME_PATTERN = "HH:mm:ss";
  public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

  // 线程安全的格式化器
  public static final DateTimeFormatter DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(DATETIME_PATTERN).withZone(ZONE_SHANGHAI);
  public static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(ZONE_SHANGHAI);
  public static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern(TIME_PATTERN).withZone(ZONE_SHANGHAI);

  // ====================== 1. 获取当前时间 ======================

  /** 获取当前 Instant（UTC时间戳，推荐用于存储、计算、传输） */
  public static Instant now() {
    return Instant.now();
  }

  /** 获取当前毫秒时间戳 */
  public static long currentMillis() {
    return System.currentTimeMillis();
  }

  /** 获取当前北京时间（LocalDateTime） */
  public static LocalDateTime nowLocalDateTime() {
    return LocalDateTime.now(ZONE_SHANGHAI);
  }

  // ====================== 2. 类型转换 ======================

  /** Date → Instant（兼容旧代码） */
  public static Instant toInstant(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant();
  }

  /** 毫秒时间戳 → Instant */
  public static Instant toInstant(long millis) {
    return Instant.ofEpochMilli(millis);
  }

  /** Instant → 北京时间 LocalDateTime */
  public static LocalDateTime toLocalDateTime(Instant instant) {
    if (instant == null) {
      return null;
    }
    return LocalDateTime.ofInstant(instant, ZONE_SHANGHAI);
  }

  /** LocalDateTime → Instant（北京时间 → UTC） */
  public static Instant toInstant(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return localDateTime.atZone(ZONE_SHANGHAI).toInstant();
  }

  // ====================== 3. 时间格式化（Instant → 字符串） ======================

  /** 格式化为：yyyy-MM-dd HH:mm:ss */
  public static String formatDateTime(Instant instant) {
    if (instant == null) {
      return "";
    }
    return DATETIME_FORMATTER.format(instant);
  }

  /** 格式化为：yyyy-MM-dd */
  public static String formatDate(Instant instant) {
    if (instant == null) {
      return "";
    }
    return DATE_FORMATTER.format(instant);
  }

  /** 格式化为：HH:mm:ss */
  public static String formatTime(Instant instant) {
    if (instant == null) {
      return "";
    }
    return TIME_FORMATTER.format(instant);
  }

  // ====================== 4. 字符串解析（字符串 → Instant） ======================

  /** 解析 yyyy-MM-dd HH:mm:ss → Instant */
  public static Instant parseDateTime(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    LocalDateTime localDateTime = LocalDateTime.parse(text, DATETIME_FORMATTER);
    return toInstant(localDateTime);
  }

  /** 解析 yyyy-MM-dd 00:00:00 → Instant */
  public static Instant parseDate(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    LocalDate localDate = LocalDate.parse(text, DATE_FORMATTER);
    return toInstant(localDate.atStartOfDay());
  }

  // ====================== 5. 时间差计算 ======================

  public static long betweenSeconds(Instant start, Instant end) {
    return ChronoUnit.SECONDS.between(start, end);
  }

  public static long betweenMinutes(Instant start, Instant end) {
    return ChronoUnit.MINUTES.between(start, end);
  }

  public static long betweenHours(Instant start, Instant end) {
    return ChronoUnit.HOURS.between(start, end);
  }

  public static long betweenDays(Instant start, Instant end) {
    return ChronoUnit.DAYS.between(start, end);
  }

  // ====================== 6. 时间加减 ======================

  public static Instant plusSeconds(Instant instant, long seconds) {
    return instant.plusSeconds(seconds);
  }

  public static Instant plusMinutes(Instant instant, long minutes) {
    return instant.plus(minutes, ChronoUnit.MINUTES);
  }

  public static Instant plusHours(Instant instant, long hours) {
    return instant.plus(hours, ChronoUnit.HOURS);
  }

  public static Instant plusDays(Instant instant, long days) {
    return instant.plus(days, ChronoUnit.DAYS);
  }

  public static Instant minusDays(Instant instant, long days) {
    return instant.minus(days, ChronoUnit.DAYS);
  }

  // ====================== 7. 常用快捷方法 ======================

  /** 今天 00:00:00 */
  public static Instant todayStart() {
    return LocalDate.now(ZONE_SHANGHAI).atStartOfDay(ZONE_SHANGHAI).toInstant();
  }

  /** 今天 23:59:59.999 */
  public static Instant todayEnd() {
    return LocalDate.now(ZONE_SHANGHAI).atTime(LocalTime.MAX).atZone(ZONE_SHANGHAI).toInstant();
  }

  /** 是否是过去时间 */
  public static boolean isBeforeNow(Instant instant) {
    return instant.isBefore(now());
  }

  /** 是否是未来时间 */
  public static boolean isAfterNow(Instant instant) {
    return instant.isAfter(now());
  }
}
