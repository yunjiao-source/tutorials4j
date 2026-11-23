package tutorials4j.service.ai.session;

import static org.springframework.ai.session.SessionAttributes.PRINCIPAL_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.service.ai.util.DefaultChatFacadeUtils;

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
    return DefaultChatFacadeUtils.instance
        .session()
        .client()
        .prompt()
        .user(userMessage)
        .advisors(SessionAdvisors.session(sessionId))
        .call()
        .content();
  }
}
