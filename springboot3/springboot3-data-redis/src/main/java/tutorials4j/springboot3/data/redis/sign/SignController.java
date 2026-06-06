package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口
 *
 * @author Yun Jiao
 */
@Validated
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
  private final SignService signService;

  @PostMapping("/do")
  public SignResult sign(
      @RequestHeader("X-User-Id") Long userId,
      @RequestParam(name = "source", defaultValue = "APP") String source) {
    return signService.sign(userId, LocalDate.now(), source);
  }

  @PostMapping("/do1")
  public SignResult sign1(
      @RequestHeader("X-User-Id") Long userId,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
      @RequestParam(name = "source", defaultValue = "APP") String source) {
    return signService.sign(userId, date, source);
  }

  @GetMapping("/status")
  public boolean status(
      @RequestHeader("X-User-Id") Long userId,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signService.hasSigned(userId, date);
  }

  @GetMapping("/calendar")
  public SignCalendarDTO calendar(
      @RequestHeader("X-User-Id") Long userId,
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return signService.queryCalendar(userId, date);
  }
}
