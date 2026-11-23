package tutorials4j.service.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.tool.toolsearch.index.regex.RegexToolIndex;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.service.ai.service.AuditService;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;
import tutorials4j.service.ai.support.ChatMemoryProvider;
import tutorials4j.service.ai.util.DefaultChatFacadeUtils;

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
  public DefaultChatFacadeUtils defaultChatFacadeUtils(
      ChatFacadeCache chatFacadeCache,
      ChatMemoryProvider chatMemoryProvider,
      SessionService sessionService,
      ToolCallingManager toolCallingManager,
      AuditService auditService) {
    log.trace("[SERVICE-AI] Default Chat Facade Utils");
    DefaultChatFacadeUtils.instance.setChatFacadeCache(chatFacadeCache);
    DefaultChatFacadeUtils.instance.setChatMemoryProvider(chatMemoryProvider);
    DefaultChatFacadeUtils.instance.setSessionService(sessionService);
    DefaultChatFacadeUtils.instance.setToolCallingManager(toolCallingManager);
    DefaultChatFacadeUtils.instance.setAuditService(auditService);
    DefaultChatFacadeUtils.instance.setToolIndex(new RegexToolIndex());
    return DefaultChatFacadeUtils.instance;
  }

  @Bean
  public ChatFacade chatFacade(ChatFacadeCache chatFacadeCache) {
    log.trace("[SERVICE-AI] Chat Facade");
    return chatFacadeCache.getDefault(
        builder -> {
          // Default System Text
          //          builder
          //              .defaultSystem(
          //                  s ->
          //                      s.text("You are a helpful assistant")
          //                          .metadata("assistantType", "general")
          //                          .metadata("version", "1.0"))

          // Default System Text with parameters
          //              .defaultSystem(
          //                  "You are a friendly chat bot that answers question in the voice of
          // {voice}")

          //              .defaultUser(
          //                  u -> u.text("Default user context").metadata("sessionId",
          // "default-session"));
        });
  }
}
