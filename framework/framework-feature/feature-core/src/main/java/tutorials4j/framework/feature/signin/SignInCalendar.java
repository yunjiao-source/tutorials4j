package tutorials4j.framework.feature.signin;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class SignInCalendar {
  private String account;
  private String month;
  private List<Integer> signedDays;
  private Boolean todaySigned;
  private Long monthlySignedCount;
  private Long continuousDays;
  private LocalDate queryDate;
}
