package tutorials4j.framework.message.redis.autoconfigure;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.message.redis.factory.ListMessageFactory;
import tutorials4j.framework.message.redis.properties.RedisMessageProperties;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RedisMessageProperties.class})
public class RedisMessageConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE-REDIS] Redis Message Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  ListMessageFactory listMessageFactory(
      RedisTemplate<String, String> stringRedisTemplate,
      ObjectMapperCreator creator,
      RedisMessageProperties properties) {
    log.trace("[MESSAGE-REDIS] List Message Factory");

    ObjectMapper objectMapper = creator.newInstance();
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("tutorials4j.framework.message.redis.bean")
            .allowIfSubType("java.util")
            .build();
    objectMapper.activateDefaultTyping(
        ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    ListMessageFactory.instance.setObjectMapper(objectMapper);
    ListMessageFactory.instance.setStringRedisTemplate(stringRedisTemplate);
    ListMessageFactory.instance.setListQueueOptionsMap(properties.getListQueues());
    return ListMessageFactory.instance;
  }
}
