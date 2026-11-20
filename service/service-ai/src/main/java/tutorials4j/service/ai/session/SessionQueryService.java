package tutorials4j.service.ai.session;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.session.SessionService;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.session.model.MessageDto;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class SessionQueryService {
  private final SessionService sessionService;

  public List<MessageDto> getHistory(String sessionId) {
    return sessionService.getMessages(sessionId).stream()
        .map(
            m ->
                new MessageDto(
                    m.getMessageType().getValue(),
                    m.getText(),
                    Instant.now() // 生产环境从 SessionEvent 取真实时间戳
                    ))
        .toList();
  }
}
