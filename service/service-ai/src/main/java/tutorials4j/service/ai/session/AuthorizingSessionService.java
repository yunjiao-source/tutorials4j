package tutorials4j.service.ai.session;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.Session;
import org.springframework.ai.session.SessionAttributes;
import org.springframework.ai.session.SessionEvent;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.compaction.CompactionResult;
import org.springframework.ai.session.compaction.CompactionStrategy;
import org.springframework.ai.session.compaction.CompactionTrigger;
import tutorials4j.service.ai.exception.AiServiceErrorCode;
import tutorials4j.toolkit.core.exception.UnauthorizedException;
import tutorials4j.toolkit.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class AuthorizingSessionService implements SessionService {
  private final SessionService delegate;

  private String getAccount() {
    return SecurityUtils.getAccountOptional().orElseThrow(UnauthorizedException::new);
  }

  private void assertOwner(String sessionId) {
    String account = getAccount();
    Session session = delegate.findById(sessionId);
    if (session == null) {
      throw AiServiceErrorCode.SESSION_NOT_FOUND.throwed().param("sessionId", sessionId);
    }
    String owner = (String) session.metadata().get(SessionAttributes.PRINCIPAL_NAME);
    if (!Objects.equals(owner, account)) {
      throw AiServiceErrorCode.SESSION_ACCESS_DENIED
          .throwed()
          .param("sessionId", session.id())
          .param("account", account);
    }
  }

  @Override
  public Session create(CreateSessionRequest request) {
    String account = getAccount();
    CreateSessionRequest.Builder builder =
        CreateSessionRequest.builder().timeToLive(request.timeToLive());
    if (request.id() != null) {
      builder.id(request.id());
    }
    Map<String, Object> metadata = request.metadata();
    metadata.forEach(builder::metadata);
    builder.metadata(SessionAttributes.PRINCIPAL_NAME, account);
    return delegate.create(builder.build());
  }

  @Override
  public @Nullable Session findById(String sessionId) {
    assertOwner(sessionId);
    return delegate.findById(sessionId);
  }

  @Override
  public List<Session> findByMetadata(String name, String value) {
    return delegate.findByMetadata(name, value);
  }

  @Override
  public void delete(String sessionId) {
    assertOwner(sessionId);
    delegate.delete(sessionId);
  }

  @Override
  public int deleteExpiredSessions(Instant before) {
    return delegate.deleteExpiredSessions(before);
  }

  @Override
  public void appendEvent(SessionEvent event) {
    delegate.appendEvent(event);
  }

  @Override
  public boolean appendEvents(String sessionId, List<SessionEvent> events, long expectedVersion) {
    assertOwner(sessionId);
    return delegate.appendEvents(sessionId, events, expectedVersion);
  }

  @Override
  public long getEventVersion(String sessionId) {
    assertOwner(sessionId);
    return delegate.getEventVersion(sessionId);
  }

  @Override
  public List<SessionEvent> getEvents(String sessionId, EventFilter filter) {
    assertOwner(sessionId);
    return delegate.getEvents(sessionId, filter);
  }

  @Override
  public CompactionResult compact(
      String sessionId, CompactionTrigger trigger, CompactionStrategy strategy) {
    assertOwner(sessionId);
    return delegate.compact(sessionId, trigger, strategy);
  }
}
