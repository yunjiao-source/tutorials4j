package tutorials4j.service.ai.session;

import static org.springframework.ai.session.SessionAttributes.PRINCIPAL_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.session.compaction.WindowCompactionStrategy;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class SessionOptService {
  private final ChatFacadeCache chatFacadeCache;
  private final SessionService sessionService;

  public String createSession(String userId) {
    Session session =
        sessionService.create(
            CreateSessionRequest.builder().metadata(PRINCIPAL_NAME, userId).build());
    return session.id();
  }

  public String chat(String sessionId, String userMessage) {

    // ② 调用 ChatClient，将 sessionId 绑定到本次请求
    return session()
        .client()
        .prompt()
        .user(userMessage)
        .advisors(SessionAdvisors.session(sessionId))
        .call()
        .content();
  }

  public ChatFacade session() {
    return chatFacadeCache.getDefault(
        "session",
        builder -> {
          builder.defaultAdvisors(
              SessionAdvisors.builder(sessionService)
                  // 历史超过 20 轮时触发压缩
                  .compaction(
                      new TurnCountTrigger(20),
                      // 始终保留最近 10 个完整轮次
                      WindowCompactionStrategy.builder().lastTurns(10).build())
                  .build());
        });
  }
}
