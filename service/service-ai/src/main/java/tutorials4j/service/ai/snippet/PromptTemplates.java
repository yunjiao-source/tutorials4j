package tutorials4j.service.ai.snippet;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class PromptTemplates {
  private final ChatClient chatClient;

  public String simple() {
    return chatClient
        .prompt()
        .user(
            u ->
                u.text("Tell me the names of 5 movies whose soundtrack was composed by {composer}")
                    .param("composer", "John Williams"))
        .call()
        .content();
  }

  public String templateRenderer() {
    return chatClient
        .prompt()
        .user(
            u ->
                u.text("Tell me the names of 5 movies whose soundtrack was composed by <composer>")
                    .param("composer", "John Williams"))
        .templateRenderer(
            StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
        .call()
        .content();
  }
}
