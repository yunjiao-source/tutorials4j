package tutorials4j.service.ai.advisors;

import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityChecker;
import tutorials4j.service.ai.service.AuditService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class MyAuditingToolCallingAdvisor extends ToolCallingAdvisor {

  private final AuditService auditService;

  protected MyAuditingToolCallingAdvisor(
      ToolCallingManager toolCallingManager,
      ToolExecutionEligibilityChecker toolExecutionEligibilityChecker,
      int advisorOrder,
      boolean conversationHistoryEnabled,
      AuditService auditService) {
    super(
        toolCallingManager,
        toolExecutionEligibilityChecker,
        advisorOrder,
        conversationHistoryEnabled);
    this.auditService = auditService;
  }

  @Override
  protected ChatClientResponse doAfterCall(ChatClientResponse response, CallAdvisorChain chain) {
    var toolCalls = response.chatResponse().getResult().getOutput().getToolCalls();
    for (var call : toolCalls) {
      auditService.recordIntent(call.name(), call.arguments());
    }
    return response;
  }

  public static Builder<?> builder() {
    return new Builder<>();
  }

  public static class Builder<T extends Builder<T>> extends ToolCallingAdvisor.Builder<T> {

    private AuditService audit;

    public T audit(AuditService audit) {
      this.audit = audit;
      return self();
    }

    @Override
    public MyAuditingToolCallingAdvisor build() {
      // Use the inherited fields from ToolCallingAdvisor.Builder via the protected getters.
      return new MyAuditingToolCallingAdvisor(
          getToolCallingManager(),
          getToolExecutionEligibilityChecker(),
          getAdvisorOrder(),
          isConversationHistoryEnabled(),
          audit);
    }
  }
}
