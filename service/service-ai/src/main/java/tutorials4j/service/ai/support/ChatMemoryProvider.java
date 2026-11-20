package tutorials4j.service.ai.support;

import java.util.Map;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public record ChatMemoryProvider(Map<String, ChatMemory> chatMemoryMap) {

  public ChatMemory jdbc() {
    return lookup("jdbcChatMemory");
  }

  public ChatMemory redis() {
    return lookup("redisChatMemory");
  }

  private ChatMemory lookup(String name) {
    ChatMemory chatMemory = chatMemoryMap.get(name);
    if (chatMemory == null) {
      throw new IllegalStateException(
          "ChatMemory 名称不存在：" + name + ", 仅支持的名称：" + chatMemoryMap.keySet());
    }
    return chatMemory;
  }
}
