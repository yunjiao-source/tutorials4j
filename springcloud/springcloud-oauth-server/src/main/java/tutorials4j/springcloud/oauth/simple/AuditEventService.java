package tutorials4j.springcloud.oauth.simple;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 审计事件服务，在内存中记录最近的认证审计事件，并提供查询能力。
 *
 * <p>事件列表有最大容量限制，超出时丢弃最早的事件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class AuditEventService {
  /** 审计事件内存存储列表。 */
  private final List<AuditEventCommand> eventStore = new ArrayList<>();

  /** 事件列表最大容量。 */
  private final int MAX_SIZE = 100;

  /**
   * 记录一条审计事件；事件数量超过上限时移除最早的事件。
   *
   * @param command 审计事件命令
   */
  public void record(AuditEventCommand command) {
    log.info(">>>{}", command);
    synchronized (eventStore) {
      if (eventStore.size() >= MAX_SIZE) {
        eventStore.remove(0);
      }
      eventStore.add(command);
    }
  }

  /**
   * 获取最近记录的审计事件副本列表。
   *
   * @return 审计事件列表（按记录顺序）
   */
  public List<AuditEventCommand> getRecentEvents() {
    synchronized (eventStore) {
      return new ArrayList<>(eventStore);
    }
  }
}
