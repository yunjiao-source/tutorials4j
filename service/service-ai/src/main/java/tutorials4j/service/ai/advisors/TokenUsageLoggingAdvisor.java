package tutorials4j.service.ai.advisors;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import tutorials4j.service.ai.util.Utils;

/**
 * 拦截响应并提取 response.getMetadata().getUsage() 信息，是实现 Token 消耗监控、成本核算的常用手段
 *
 * @author Yun Jiao
 */
@Slf4j
public class TokenUsageLoggingAdvisor implements CallAdvisor {
  @Override
  public String getName() {
    return "TokenUsageLoggingAdvisor";
  }

  @Override
  public int getOrder() {
    // 数值越小，执行越靠前（before阶段）；在after阶段则越靠后
    return 0;
  }

  @Override
  public ChatClientResponse adviseCall(
      ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
    // 1. 放行调用链，获取原始响应
    ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

    // 2. 后处理：提取 Usage 并打印
    ChatResponse chatResponse = chatClientResponse.chatResponse();
    if (chatResponse != null) {
      Usage usage = chatResponse.getMetadata().getUsage();
      Utils.greenSystemOut(
          StrUtil.format(
              "Token Usage - Prompt: {}, Completion: {}, Total: {}",
              usage.getPromptTokens(),
              usage.getCompletionTokens(),
              usage.getTotalTokens()));
    }
    return chatClientResponse;
  }
}
