package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

/**
 * DTO 定义
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class SignResult {
  private Long userId;
  private LocalDate signDate;
  private boolean success;
  private boolean alreadySigned;
  private int continuousDays;
  private int monthlySignedDays;
}
