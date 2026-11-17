package tutorials4j.service.ai.snippet;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class MessageMetadata {
  private final ChatClient chatClient;

  public String addingMetadataToUserMessages1() {
    // Adding individual metadata key-value pairs
    return chatClient
        .prompt()
        .user(
            u ->
                u.text("What's the weather like?")
                    .metadata("messageId", "msg-123")
                    .metadata("userId", "user-456")
                    .metadata("priority", "high"))
        .call()
        .content();
  }

  public String addingMetadataToUserMessages2() {
    // Adding multiple metadata entries at once
    Map<String, Object> userMetadata =
        Map.of(
            "messageId", "msg-123",
            "userId", "user-456",
            "timestamp", System.currentTimeMillis());

    return chatClient
        .prompt()
        .user(u -> u.text("What's the weather like?").metadata(userMetadata))
        .call()
        .content();
  }

  public String addingMetadataToSystemMessages() {
    // Adding metadata to system messages
    return chatClient
        .prompt()
        .system(
            s ->
                s.text("You are a helpful assistant.")
                    .metadata("version", "1.0")
                    .metadata("model", "gpt-4"))
        .user("Tell me a joke")
        .call()
        .content();
  }
}
