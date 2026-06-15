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
public class SignInController {
  private final SignInService signInService;

  @PostMapping("do")
  public SignInResult sign(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source) {
    return signInService.template(source).signIn(account, LocalDate.now());
  }

  @GetMapping("status")
  public boolean status(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signInService.template(source).checkStatus(account, date);
  }

  @GetMapping("daily")
  public SignInResult info(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signInService.template(source).queryDaily(account, date);
  }

  @GetMapping("calendar")
  public SignInCalendar calendar(
      @RequestHeader(DefaultConsts.HTTP_HEADER_SIGN_IN_ACCOUNT) String account,
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signInService.template(source).queryCalendar(account, date);
  }

  @GetMapping("count-daily-active")
  public Long countDailyActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signInService.template(source).countDailyActive(date);
  }

  @GetMapping("count-month-active")
  public Long countMonthActive(
      @RequestParam(name = "source") String source,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signInService.template(source).countMonthActive(date);
  }
}
