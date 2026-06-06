package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class SignEventPublisher {

  public void publish(
      String eventId, Long userId, LocalDate signDate, int continuousDays, String source) {
    log.info("publish");
  }
}
