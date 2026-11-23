package tutorials4j.service.ai.demo.order;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.session.compaction.WindowCompactionStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tutorials4j.service.ai.session.SessionOptService;
import tutorials4j.service.ai.session.model.CreateSessionResponse;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.toolkit.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
  private final SessionService sessionService;
  private final OrderTools orderTools;
  private final ChatFacadeCache chatFacadeCache;
  private final SessionOptService sessionOptService;

  @Value("classpath:prompts/order_system_prompt.st")
  private Resource systemText;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public CreateSessionResponse createSession() {
    String sessionId = sessionOptService.createSession(SecurityUtils.getAccount());
    return new CreateSessionResponse(sessionId);
  }

  @GetMapping("/{sessionId}/chat")
  public OrderChat newChat(
      @PathVariable String sessionId,
      String userId,
      String orderId,
      HttpServletResponse httpServletResponse) {
    httpServletResponse.setCharacterEncoding("UTF-8");

    return order()
        .client()
        .prompt()
        .user(
            String.format(
                "我要咨询订单相关的售后问题，我的用户id是%s,我的订单号是: %s ,当前状态是 %s",
                userId, orderId, ChatStatus.CHAT_START.name()))
        .advisors(SessionAdvisors.session(sessionId))
        .call()
        .entity(OrderChat.class);
  }

  @GetMapping(
      value = "/{sessionId}/ask",
      produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
  public Flux<String> ask(
      @PathVariable String sessionId, String question, HttpServletResponse httpServletResponse) {
    httpServletResponse.setCharacterEncoding("UTF-8");

    return order()
        .client()
        .prompt()
        .user(question)
        .tools(orderTools)
        .advisors(SessionAdvisors.session(sessionId))
        .stream()
        .content();
  }

  public ChatFacade order() {
    return chatFacadeCache.getDefault(
        "order",
        builder -> {
          builder
              .defaultSystem(systemText)
              .defaultAdvisors(
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
