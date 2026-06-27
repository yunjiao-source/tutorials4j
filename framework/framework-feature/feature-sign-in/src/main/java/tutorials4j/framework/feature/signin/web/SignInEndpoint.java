package tutorials4j.framework.feature.signin.web;

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
import tutorials4j.framework.feature.signin.SignInCalendar;
import tutorials4j.framework.feature.signin.SignInResult;
import tutorials4j.framework.feature.signin.SignInService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/sign-in")
@RequiredArgsConstructor
public class SignInEndpoint {
  private final SignInService signInService;

  @PostMapping("do")
  public Result<SignInResult> sign(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source) {
    SignInResult result = signInService.template(source).signIn(account, LocalDate.now());
    return Result.success(result);
  }

  @GetMapping("status")
  public Result<Boolean> status(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Boolean result = signInService.template(source).checkStatus(account, date);
    return Result.success(result);
  }

  @GetMapping("daily")
  public Result<SignInResult> info(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    SignInResult result = signInService.template(source).queryDaily(account, date);
    return Result.success(result);
  }

  @GetMapping("calendar")
  public Result<SignInCalendar> calendar(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    SignInCalendar result = signInService.template(source).queryCalendar(account, date);
    return Result.success(result);
  }

  @GetMapping("count-daily-active")
  public Result<Long> countDailyActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Long result = signInService.template(source).countDailyActive(date);
    return Result.success(result);
  }

  @GetMapping("count-month-active")
  public Result<Long> countMonthActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    Long result = signInService.template(source).countMonthActive(date);
    return Result.success(result);
  }
}
