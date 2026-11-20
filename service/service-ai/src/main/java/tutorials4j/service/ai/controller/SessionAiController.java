package tutorials4j.service.ai.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.session.SessionOptService;
import tutorials4j.service.ai.session.SessionQueryService;
import tutorials4j.service.ai.session.model.ChatRequest;
import tutorials4j.service.ai.session.model.ChatResponse;
import tutorials4j.service.ai.session.model.CreateSessionResponse;
import tutorials4j.service.ai.session.model.HistoryResponse;
import tutorials4j.service.ai.session.model.MessageDto;
import tutorials4j.toolkit.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("session")
@RequiredArgsConstructor
public class SessionAiController {
  private final SessionOptService sessionOptService;
  private final SessionQueryService sessionQueryService;

  /**
   * 创建会话。服务端生成 sessionId，并将当前用户绑定为 PRINCIPAL_NAME。 生产环境 user 应来自 Spring Security 的
   * Authentication，而非请求头。
   */
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public CreateSessionResponse createSession() {
    String sessionId = sessionOptService.createSession(SecurityUtils.getAccount());
    return new CreateSessionResponse(sessionId);
  }

  /** 在指定会话中对话。sessionId 作为路径变量，天然要求客户端显式携带。 */
  @PostMapping("/{sessionId}/chat")
  public ChatResponse chat(@PathVariable String sessionId, @RequestBody ChatRequest request) {
    String reply = sessionOptService.chat(sessionId, request.message());
    return new ChatResponse(sessionId, reply);
  }

  /** 查询会话历史（活跃视图，已应用压缩策略）。 用于前端刷新后恢复对话、或运维排查。 */
  @GetMapping("/{sessionId}/history")
  public HistoryResponse history(@PathVariable String sessionId) {
    List<MessageDto> messages = sessionQueryService.getHistory(sessionId);
    return new HistoryResponse(sessionId, messages);
  }
}
