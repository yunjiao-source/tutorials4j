package tutorials4j.service.ai.session;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.session.autoconfigure.SessionServiceProperties;
import org.springframework.ai.session.SessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionCleanupJob {
  private final SessionService sessionService;

  private final SessionServiceProperties properties;

  // 每天早上 9 点清理过期会话，与 time-to-live 配合使用
  @Scheduled(cron = "0 0 9 * * *")
  public void purgeExpiredSessions() {
    int deleted =
        sessionService.deleteExpiredSessions(Instant.now().minus(properties.getTimeToLive()));
    // 接入你的监控系统
    if (deleted > 0) {
      log.info("Purged {} expired sessions", deleted);
    }
  }
}
