package tutorials4j.service.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.model.chat.memory.repository.redis.autoconfigure.RedisChatMemoryRepositoryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.RedisClient;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisChatMemoryRepository.class)
public class RedisChatMemoryConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-AI] Redis Chat Memory Configuration");
  }

  /**
   * 从 RedisChatMemoryRepositoryAutoConfiguration 复制的代码
   *
   * @param jedisClient
   * @param properties
   * @return
   */
  @Bean
  @ConditionalOnMissingBean
  public RedisChatMemoryRepository redisChatMemoryRepository(
      RedisClient jedisClient, RedisChatMemoryRepositoryProperties properties) {
    RedisChatMemoryRepository.Builder builder =
        RedisChatMemoryRepository.builder().jedisClient(jedisClient);
    if (StringUtils.hasText(properties.getIndexName())) {
      builder.indexName(properties.getIndexName());
    }

    if (StringUtils.hasText(properties.getKeyPrefix())) {
      builder.keyPrefix(properties.getKeyPrefix());
    }

    if (properties.getTimeToLive() != null && properties.getTimeToLive().toSeconds() > 0L) {
      builder.timeToLive(properties.getTimeToLive());
    }

    if (properties.getInitializeSchema() != null) {
      builder.initializeSchema(properties.getInitializeSchema());
    }

    if (properties.getMaxConversationIds() != null) {
      builder.maxConversationIds(properties.getMaxConversationIds());
    }

    if (properties.getMaxMessagesPerConversation() != null) {
      builder.maxMessagesPerConversation(properties.getMaxMessagesPerConversation());
    }

    if (properties.getMetadataFields() != null && !properties.getMetadataFields().isEmpty()) {
      builder.metadataFields(properties.getMetadataFields());
    }

    return builder.build();
  }

  @Bean("redisChatMemory")
  public ChatMemory redisChatMemory(RedisChatMemoryRepository redisChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(redisChatMemoryRepository)
        .maxMessages(20)
        .build();
  }
}
