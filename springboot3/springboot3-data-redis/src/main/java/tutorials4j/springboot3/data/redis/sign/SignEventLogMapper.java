package tutorials4j.springboot3.data.redis.sign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class SignEventLogMapper {

  public void insert(SignEventLogDO eventLog) {
    log.info("insert:{}", eventLog);
  }
}
