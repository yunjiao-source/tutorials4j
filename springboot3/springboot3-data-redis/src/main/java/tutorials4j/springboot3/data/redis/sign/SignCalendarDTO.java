package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * DTO 定义
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class SignCalendarDTO {
  private Long userId;
  private String month;
  private List<Integer> signedDays;
  private boolean todaySigned;
  private int monthlySignedCount;
  private int continuousDays;
  private LocalDate queryDate;
}
