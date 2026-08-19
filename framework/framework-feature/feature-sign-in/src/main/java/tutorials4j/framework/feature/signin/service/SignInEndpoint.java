package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.bean.Result;

/**
 * 签到 REST 接口
 *
 * <p>提供签到、签到状态查询、每日签到信息、签到日历以及日活/月活统计等 HTTP 接口， 通过 {@link SignInTemplateFactory} 按来源获取对应的 {@link
 * SignInTemplate} 执行具体逻辑。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/sign-in")
@RequiredArgsConstructor
public class SignInEndpoint {
  private final SignInTemplateFactory signInTemplateFactory;

  /**
   * 执行签到
   *
   * @param account 签到账号，通过请求头 {@code HTTP_HEADER_SIGN_IN_ACCOUNT} 传递
   * @param source 签到来源标识
   * @return 签到结果
   */
  @PostMapping("do")
  public Result<SignInResult> sign(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source) {
    SignInResult result = signInTemplateFactory.template(source).signIn(account, LocalDate.now());
    return Result.success(result);
  }

  /**
   * 查询指定账号在指定日期是否已签到
   *
   * @param account 签到账号，通过请求头 {@code HTTP_HEADER_SIGN_IN_ACCOUNT} 传递
   * @param source 签到来源标识
   * @param date 查询日期（yyyy-MM-dd）
   * @return 是否已签到
   */
  @GetMapping("status")
  public Result<Boolean> status(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Boolean result = signInTemplateFactory.template(source).checkStatus(account, date);
    return Result.success(result);
  }

  /**
   * 查询指定账号在指定日期的签到详情
   *
   * @param account 签到账号，通过请求头 {@code HTTP_HEADER_SIGN_IN_ACCOUNT} 传递
   * @param source 签到来源标识
   * @param date 查询日期（yyyy-MM-dd）
   * @return 签到详情
   */
  @GetMapping("daily")
  public Result<SignInResult> info(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    SignInResult result = signInTemplateFactory.template(source).queryDaily(account, date);
    return Result.success(result);
  }

  /**
   * 查询指定账号在指定月份的签到日历
   *
   * @param account 签到账号，通过请求头 {@code HTTP_HEADER_SIGN_IN_ACCOUNT} 传递
   * @param source 签到来源标识
   * @param date 查询日期（yyyy-MM-dd），用于确定所属月份
   * @return 签到日历数据
   */
  @GetMapping("calendar")
  public Result<SignInCalendar> calendar(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    SignInCalendar result = signInTemplateFactory.template(source).queryCalendar(account, date);
    return Result.success(result);
  }

  /**
   * 统计指定日期的日活（DAU）签到人数
   *
   * @param source 签到来源标识
   * @param date 统计日期（yyyy-MM-dd）
   * @return 日活签到人数
   */
  @GetMapping("count-daily-active")
  public Result<Long> countDailyActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Long result = signInTemplateFactory.template(source).countDailyActive(date);
    return Result.success(result);
  }

  /**
   * 统计指定日期的月活（MAU）签到人数
   *
   * @param source 签到来源标识
   * @param date 统计日期（yyyy-MM-dd），用于确定所属月份
   * @return 月活签到人数
   */
  @GetMapping("count-month-active")
  public Result<Long> countMonthActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Long result = signInTemplateFactory.template(source).countMonthActive(date);
    return Result.success(result);
  }
}
