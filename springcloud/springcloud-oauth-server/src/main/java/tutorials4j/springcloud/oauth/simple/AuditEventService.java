package tutorials4j.springcloud.oauth.simple;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class AuditEventService {
  private final List<AuditEventCommand> eventStore = new ArrayList<>();
  private final int MAX_SIZE = 100;

  public void record(AuditEventCommand command) {
    log.info(">>>{}", command);
    synchronized (eventStore) {
      if (eventStore.size() >= MAX_SIZE) {
        eventStore.remove(0);
      }
      eventStore.add(command);
    }
  }

  public List<AuditEventCommand> getRecentEvents() {
    synchronized (eventStore) {
      return new ArrayList<>(eventStore);
    }
  }
}
