package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 签到服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {
  private final StringRedisTemplate stringRedisTemplate;
  private final RedisScript<Long> signInScript;
  private final SignEventLogMapper signEventLogMapper;
  private final SignEventPublisher signEventPublisher;

  @Value("${sign.cache.expire-days:400}")
  private int expireDays;

  @Transactional(rollbackFor = Exception.class)
  public SignResult sign(Long userId, LocalDate signDate, String source) {
    String key = SignKeyBuilder.monthlyKey(userId, signDate);
    int offset = SignKeyBuilder.offset(signDate);
    long expireSeconds = TimeUnit.DAYS.toSeconds(expireDays);

    Long scriptResult =
        stringRedisTemplate.execute(
            signInScript,
            Collections.singletonList(key),
            String.valueOf(offset),
            String.valueOf(expireSeconds));

    boolean success = Long.valueOf(1L).equals(scriptResult);
    if (!success) {
      int continuousDays = calculateContinuousDays(userId, signDate);
      int monthlyCount = monthlySignedCount(userId, signDate);
      return SignResult.builder()
          .userId(userId)
          .signDate(signDate)
          .success(false)
          .alreadySigned(true)
          .continuousDays(continuousDays)
          .monthlySignedDays(monthlyCount)
          .build();
    }

    int continuousDays = calculateContinuousDays(userId, signDate);
    int monthlyCount = monthlySignedCount(userId, signDate);
    String eventId = UUID.randomUUID().toString().replace("-", "");

    SignEventLogDO eventLog =
        SignEventLogDO.of(
            eventId,
            userId,
            signDate,
            signDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")),
            source,
            continuousDays,
            0);

    signEventLogMapper.insert(eventLog);

    signEventPublisher.publish(eventId, userId, signDate, continuousDays, source);

    return SignResult.builder()
        .userId(userId)
        .signDate(signDate)
        .success(true)
        .alreadySigned(false)
        .continuousDays(continuousDays)
        .monthlySignedDays(monthlyCount)
        .build();
  }

  public boolean hasSigned(Long userId, LocalDate date) {
    String key = SignKeyBuilder.monthlyKey(userId, date);
    int offset = SignKeyBuilder.offset(date);
    Boolean result = stringRedisTemplate.opsForValue().getBit(key, offset);
    return Boolean.TRUE.equals(result);
  }

  public int calculateContinuousDays(Long userId, LocalDate date) {
    String key = SignKeyBuilder.monthlyKey(userId, date);
    int day = date.getDayOfMonth();

    List<Long> result =
        stringRedisTemplate
            .opsForValue()
            .bitField(
                key,
                BitFieldSubCommands.create()
                    .get(BitFieldSubCommands.BitFieldType.unsigned(day))
                    .valueAt(0));

    if (result == null || result.isEmpty() || result.get(0) == null) {
      return 0;
    }

    long value = result.get(0);
    int count = 0;
    for (int i = 0; i < day; i++) {
      if ((value & 1) == 0) {
        break;
      }
      count++;
      value >>= 1;
    }
    return count;
  }

  public int monthlySignedCount(Long userId, LocalDate date) {
    String key = SignKeyBuilder.monthlyKey(userId, date);
    Long count =
        stringRedisTemplate.execute(
            (RedisCallback<Long>)
                connection -> connection.stringCommands().bitCount(key.getBytes()));
    return count == null ? 0 : count.intValue();
  }

  public SignCalendarDTO queryCalendar(Long userId, LocalDate date) {
    YearMonth yearMonth = YearMonth.from(date);
    String key = SignKeyBuilder.monthlyKey(userId, date);
    int daysInMonth = yearMonth.lengthOfMonth();

    List<Integer> signedDays = new ArrayList<>();
    for (int i = 0; i < daysInMonth; i++) {
      Boolean signed = stringRedisTemplate.opsForValue().getBit(key, i);
      if (Boolean.TRUE.equals(signed)) {
        signedDays.add(i + 1);
      }
    }

    return SignCalendarDTO.builder()
        .userId(userId)
        .month(yearMonth.toString())
        .signedDays(signedDays)
        .todaySigned(hasSigned(userId, date))
        .monthlySignedCount(monthlySignedCount(userId, date))
        .continuousDays(calculateContinuousDays(userId, date))
        .queryDate(date)
        .build();
  }
}
