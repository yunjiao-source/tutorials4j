package tutorials4j.framework.feature.signin;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.cache.redis.util.RedisBitmapUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SignInTemplate {
  // 默认10万用户
  private static final int MAX_BITS = 100_000;
  private final SignInResultHandler signInResultHandler;
  private final SignInConfig config;

  public SignInResult signIn(String account, LocalDate signDate) {
    Assert.hasText(account, "account must not be null or empty");
    Assert.notNull(signDate, "date must not be null");

    // 签到信息
    String monthKey = getMonthKey(account, signDate);
    long offset = getOffset(signDate);
    boolean firstSigned = doSign(monthKey, offset);
    if (firstSigned) {
      // 日活信息
      doActive(signDate, account);
    }

    Long monthlyCount = RedisBitmapUtils.instance.bitCount(monthKey);
    long continuousDays = calculateContinuousDays(monthKey, signDate);
    SignInResult signInResult =
        SignInResult.builder()
            .account(account)
            .signDate(signDate)
            .source(config.source())
            .signedIn(firstSigned)
            .repeatedSignIn(!firstSigned)
            .continuousDays(continuousDays)
            .monthlySignedDays(monthlyCount)
            .build();
    signInResultHandler.handle(signInResult);

    return signInResult;
  }

  public SignInResult queryDaily(String account, LocalDate date) {
    Assert.hasText(account, "account must not be null or empty");
    Assert.notNull(date, "date must not be null");

    String monthKey = getMonthKey(account, date);
    Long monthlyCount = RedisBitmapUtils.instance.bitCount(monthKey);
    long continuousDays = calculateContinuousDays(monthKey, date);
    return SignInResult.builder()
        .account(account)
        .signDate(date)
        .source(config.source())
        .signedIn(checkStatus(account, date))
        .continuousDays(continuousDays)
        .monthlySignedDays(monthlyCount)
        .build();
  }

  public SignInCalendar queryCalendar(String account, LocalDate date) {
    YearMonth yearMonth = YearMonth.from(date);
    String monthKey = getMonthKey(account, date);
    int daysInMonth = yearMonth.lengthOfMonth();

    List<Integer> signedDays = new ArrayList<>();
    for (int i = 0; i < daysInMonth; i++) {
      Boolean signed = RedisBitmapUtils.instance.getBit(monthKey, i);
      if (Boolean.TRUE.equals(signed)) {
        signedDays.add(i + 1);
      }
    }

    Long monthlyCount = RedisBitmapUtils.instance.bitCount(monthKey);
    long continuousDays = calculateContinuousDays(monthKey, date);
    return SignInCalendar.builder()
        .account(account)
        .month(yearMonth.toString())
        .signedDays(signedDays)
        .todaySigned(checkStatus(account, date))
        .monthlySignedCount(monthlyCount)
        .continuousDays(continuousDays)
        .queryDate(date)
        .build();
  }

  public boolean checkStatus(String account, LocalDate date) {
    Assert.hasText(account, "account must not be null or empty");
    Assert.notNull(date, "date must not be null");

    String monthKey = getMonthKey(account, date);
    long offset = getOffset(date);
    return RedisBitmapUtils.instance.getBit(monthKey, offset);
  }

  public long countDailyActive(LocalDate date) {
    Assert.notNull(date, "date must not be null");
    String dauKey = getDauKey(date);
    Long count = RedisBitmapUtils.instance.bitCount(dauKey);
    return count == null ? 0L : count;
  }

  public long countMonthActive(LocalDate date) {
    Assert.notNull(date, "date must not be null");
    String dauKey = getDauKey(date);

    Long count = RedisBitmapUtils.instance.bitCount(dauKey);
    return count == null ? 0L : count;
  }

  private boolean doSign(String key, long offset) {
    Boolean oldValue = RedisBitmapUtils.instance.setBit(key, offset, true);
    boolean success = Objects.equals(oldValue, Boolean.FALSE);
    if (success) {
      // 首次
      RedisBitmapUtils.instance.setExpireTime(key, config.expireTime());
    }
    return success;
  }

  private void doActive(LocalDate date, String account) {
    long offset = Math.abs(RedisBitmapUtils.instance.hash(account) / MAX_BITS);
    String dauKey = getDauKey(date);
    Boolean oldValue = RedisBitmapUtils.instance.setBit(dauKey, offset, true);
    boolean success = Objects.equals(oldValue, Boolean.FALSE);
    if (success) {
      // 首次
      RedisBitmapUtils.instance.setExpireTime(dauKey, config.expireTime());
    }

    String mauKey = getMauKey(date);
    oldValue = RedisBitmapUtils.instance.setBit(mauKey, offset, true);
    success = Objects.equals(oldValue, Boolean.FALSE);
    if (success) {
      // 首次
      RedisBitmapUtils.instance.setExpireTime(mauKey, config.expireTime());
    }
  }

  private String getMonthKey(String account, LocalDate date) {
    return SignInUtils.monthlyKey(config.keyPrefix(), config.source(), account, date);
  }

  private long getOffset(LocalDate date) {
    return SignInUtils.offset(date);
  }

  private String getDauKey(LocalDate date) {
    return SignInUtils.dauKey(config.keyPrefix(), config.source(), date);
  }

  private String getMauKey(LocalDate date) {
    return SignInUtils.mauKey(config.keyPrefix(), config.source(), date);
  }

  private long calculateContinuousDays(String key, LocalDate date) {
    int day = date.getDayOfMonth();

    List<Long> result =
        RedisBitmapUtils.instance
            .getStringRedisTemplate()
            .opsForValue()
            .bitField(
                key,
                BitFieldSubCommands.create()
                    .get(BitFieldSubCommands.BitFieldType.unsigned(day))
                    .valueAt(0));

    if (CollectionUtils.isEmpty(result)) {
      return 0;
    }

    long value = result.getFirst();
    long count = 0;
    for (int i = 0; i < day; i++) {
      if ((value & 1) == 0) {
        break;
      }
      count++;
      value >>= 1;
    }
    return count;
  }
}
