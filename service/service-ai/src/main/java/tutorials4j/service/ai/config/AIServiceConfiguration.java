package tutorials4j.service.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.service.ai.support.ChatClientBuilderProvider;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  AIServiceProperties.class,
})
public class AIServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-AI] AI Service Configuration");
  }

  @Bean
  public ChatClient chatClient(ChatClientBuilderProvider provider) {
    return provider
        .getBuilderDefault()
        .create()
        // Default System Text
        //        .defaultSystem(s -> s.text("You are a helpful assistant")
        //            .metadata("assistantType", "general")
        //            .metadata("version", "1.0"))

        // Default System Text with parameters
        // .defaultSystem("You are a friendly chat bot that answers question in the voice of a
        // {voice}")

        //        .defaultUser(u -> u.text("Default user context")
        //            .metadata("sessionId", "default-session"))
        .build();
  }
}
