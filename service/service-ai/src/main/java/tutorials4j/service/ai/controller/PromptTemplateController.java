package tutorials4j.service.ai.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("prompt-template")
@RequiredArgsConstructor
public class PromptTemplateController {
  private final ChatClient defaultChatClient;

  @Value("classpath:/prompts/system-message.st")
  private Resource systemResource;

  @GetMapping("using-a-custom-template-renderer")
  public Map<String, String> usingCustomTemplateRenderer() {

    PromptTemplate promptTemplate =
        PromptTemplate.builder()
            .renderer(
                StTemplateRenderer.builder()
                    .startDelimiterToken('<')
                    .endDelimiterToken('>')
                    .build())
            .template(
                """
            Tell me the names of 5 movies whose soundtrack was composed by <composer>.
            """)
            .build();

    String prompt = promptTemplate.render(Map.of("composer", "John Williams"));

    String content = defaultChatClient.prompt(prompt).call().content();
    return Map.of("completion", content);
  }

  @GetMapping("workshop-on-roles")
  public Map<String, String> workshopOnRoles() {

    String userText =
        """
    Tell me about three famous pirates from the Golden Age of Piracy and why they did.
    Write at least a sentence for each pirate.
    """;

    Message userMessage = new UserMessage(userText);

    String systemText =
        """
  You are a helpful AI assistant that helps people find information.
  Your name is {name}
  You should reply to the user's request with your name and also in the style of a {voice}.
  """;

    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
    // 或使用外部文件注入
    // SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
    Message systemMessage =
        systemPromptTemplate.createMessage(
            Map.of("name", "豆包", "voice", "professional and concise"));

    Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

    String content = defaultChatClient.prompt(prompt).call().content();
    return Map.of("completion", content);
  }

  @GetMapping("workshop-on-prompt-templates")
  public Map<String, String> workshopOnPromptTemplates() {

    PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");

    Prompt prompt = promptTemplate.create(Map.of("adjective", "cold", "topic", "programmers"));

    String content = defaultChatClient.prompt(prompt).call().content();
    return Map.of("completion", content);
  }
}
