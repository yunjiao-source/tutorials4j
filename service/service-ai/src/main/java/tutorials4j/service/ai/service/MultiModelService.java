package tutorials4j.service.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.stereotype.Service;

/**
 * Multiple OpenAI-Compatible API Endpoints
 *
 * <p>可以使用各自的构建器创建多个 {@code OpenAiChatModel} 实例，以连接不同的 OpenAI 兼容 API。当需要与多个提供商协作时，这尤其有用。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class MultiModelService {
  public void multiClientFlow() {
    try {
      DeepSeekChatModel deepSeekModel =
          DeepSeekChatModel.builder()
              .options(
                  DeepSeekChatOptions.builder()
                      // .baseUrl("https://api.groq.com/openai/v1")
                      // .apiKey(System.getenv("DS_API_KEY"))
                      .model(DeepSeekApi.ChatModel.DEEPSEEK_V4_FLASH.getValue())
                      .temperature(0.5)
                      .build())
              .build();

      // Simple prompt for both models
      String prompt = "What is the capital of France?";

      String deepseekResponse =
          ChatClient.builder(deepSeekModel).build().prompt(prompt).call().content();

      log.info("DeepSeek response: {}", deepseekResponse);
    } catch (Exception e) {
      log.error("Error in multi-client flow", e);
    }
  }
}
