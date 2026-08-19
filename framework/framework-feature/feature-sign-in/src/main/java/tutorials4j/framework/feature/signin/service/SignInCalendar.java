package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 签到日历数据
 *
 * <p>承载指定账号在指定月份的签到明细（已签到日期列表）、当月签到次数、连续签到天数等信息， 用于前端签到日历视图的展示。
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
