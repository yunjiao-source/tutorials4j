package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record SignEventLogDO(
    String eventId,
    Long userId,
    LocalDate signDate,
    String signMonth,
    String source,
    Integer continuousDays,
    Integer rewardStatus) {

  public static SignEventLogDO of(
      String eventId,
      Long userId,
      LocalDate signDate,
      String signMonth,
      String source,
      Integer continuousDays,
      Integer rewardStatus) {
    return new SignEventLogDO(
        eventId, userId, signDate, signMonth, source, continuousDays, rewardStatus);
  }
}
