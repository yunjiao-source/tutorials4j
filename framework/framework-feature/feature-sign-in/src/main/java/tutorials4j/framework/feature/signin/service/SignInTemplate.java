package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.cache.redis.RedisTemplateDecorator;
import tutorials4j.framework.cache.redis.util.RedisBitmapUtils;

/**
 * 签到模板
 *
 * <p>基于 Redis 位图（Bitmap）实现签到、签到状态查询、签到日历、日活/月活统计等核心功能， 每个来源（source）对应一个独立实例，签到完成后通过注册的 {@link
 * SignInResultHandler} 进行后续处理。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SignInTemplate {
  private final List<SignInResultHandler> signInResultHandlers;
  private final SignInConfig config;

  /**
   * 执行签到
   *
   * <p>在当月位图上标记签到位，首次签到时会同步记录日活/月活信息，并通知注册的签到结果处理器。
   *
   * @param account 签到账号
   * @param signDate 签到日期
   * @return 签到结果
   * @throws IllegalArgumentException 当 account 为空或 signDate 为 null 时
   */
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
    notifySignInResultHandlers(signInResult);

    return signInResult;
  }

  /**
   * 查询指定账号在指定日期的签到详情
   *
   * @param account 签到账号
   * @param date 查询日期
   * @return 签到详情
   * @throws IllegalArgumentException 当 account 为空或 date 为 null 时
   */
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

  /**
   * 查询指定账号在指定日期所属月份的签到日历
   *
   * @param account 签到账号
   * @param date 查询日期，用于确定所属月份
   * @return 签到日历数据
   */
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

  /**
   * 查询指定账号在指定日期是否已签到
   *
   * @param account 签到账号
   * @param date 查询日期
   * @return 是否已签到
   * @throws IllegalArgumentException 当 account 为空或 date 为 null 时
   */
  public boolean checkStatus(String account, LocalDate date) {
    Assert.hasText(account, "account must not be null or empty");
    Assert.notNull(date, "date must not be null");

    String monthKey = getMonthKey(account, date);
    long offset = getOffset(date);
    return RedisBitmapUtils.instance.getBit(monthKey, offset);
  }

  /**
   * 统计指定日期的日活（DAU）签到人数
   *
   * @param date 统计日期
   * @return 日活签到人数
   * @throws IllegalArgumentException 当 date 为 null 时
   */
  public long countDailyActive(LocalDate date) {
    Assert.notNull(date, "date must not be null");
    String dauKey = getDauKey(date);
    Long count = RedisBitmapUtils.instance.bitCount(dauKey);
    return count == null ? 0L : count;
  }

  /**
   * 统计指定日期所属月份的月活（MAU）签到人数
   *
   * @param date 统计日期，用于确定所属月份
   * @return 月活签到人数
   * @throws IllegalArgumentException 当 date 为 null 时
   */
  public long countMonthActive(LocalDate date) {
    Assert.notNull(date, "date must not be null");
    String dauKey = getDauKey(date);

    Long count = RedisBitmapUtils.instance.bitCount(dauKey);
    return count == null ? 0L : count;
  }

  /** 在月度签到位图上标记指定偏移量的签到位，返回是否为首次签到 */
  private boolean doSign(String key, long offset) {
    Boolean oldValue = RedisBitmapUtils.instance.setBit(key, offset, true);
    boolean success = Objects.equals(oldValue, Boolean.FALSE);
    if (success) {
      // 首次
      RedisBitmapUtils.instance.setExpireTime(key, config.expireTime());
    }
    return success;
  }

  /** 记录账号的日活（DAU）与月活（MAU）位图标记，首次标记时设置过期时间 */
  private void doActive(LocalDate date, String account) {
    long offset = Math.abs(RedisBitmapUtils.instance.hash(account) / config.maxBits());
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

  /** 通过位域运算计算从签到日期起连续的签到天数 */
  private long calculateContinuousDays(String key, LocalDate date) {
    int day = date.getDayOfMonth();

    List<Long> result =
        RedisTemplateDecorator.stringRedisTemplate()
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

  /** 通知所有已注册的签到结果处理器，单个处理器异常不影响其他处理器的执行 */
  private void notifySignInResultHandlers(SignInResult data) {
    if (signInResultHandlers == null || signInResultHandlers.isEmpty()) {
      log.warn("没有注册签到事件处理器'{}'，SignInResult={}", SignInResultHandler.class.getSimpleName(), data);
      return;
    }

    try {
      for (SignInResultHandler signInResultHandler : signInResultHandlers) {
        signInResultHandler.handle(data);
      }
    } catch (Exception e) {
      log.error("处理任务事件异常", e);
    }
  }
}
