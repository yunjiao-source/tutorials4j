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
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.message.redis.support.list.ListMessageTempalteFactory;
import tutorials4j.framework.message.redis.support.properties.RedisMessageProperties;
import tutorials4j.framework.message.redis.support.stream.StreamMessageTempalteFactory;
import tutorials4j.framework.message.redis.support.zset.ZSetMessageTemplateFactory;

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
  StreamMessageTempalteFactory streamMessageTempalteFactory(
      StringRedisTemplate stringRedisTemplate,
      ObjectMapperCreator creator,
      RedisMessageProperties properties) {
    log.trace("[MESSAGE-REDIS] Stream Message Tempalte Factory");

    ObjectMapper objectMapper = createObjectMapper(creator);
    StreamMessageTempalteFactory.instance.setJacksonRecord(new JacksonRecord(objectMapper));
    StreamMessageTempalteFactory.instance.setStringRedisTemplate(stringRedisTemplate);
    StreamMessageTempalteFactory.instance.setQueueOptionsMap(properties.getQueues());
    return StreamMessageTempalteFactory.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  ZSetMessageTemplateFactory zsetMessageTemplateFactory(
      StringRedisTemplate stringRedisTemplate,
      ObjectMapperCreator creator,
      RedisMessageProperties properties) {
    log.trace("[MESSAGE-REDIS] ZSet Message TemplateF actory");

    ObjectMapper objectMapper = createObjectMapper(creator);
    ZSetMessageTemplateFactory.instance.setJacksonRecord(new JacksonRecord(objectMapper));
    ZSetMessageTemplateFactory.instance.setStringRedisTemplate(stringRedisTemplate);
    ZSetMessageTemplateFactory.instance.setQueueOptionsMap(properties.getQueues());
    return ZSetMessageTemplateFactory.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  ListMessageTempalteFactory listMessageTempalteFactory(
      StringRedisTemplate stringRedisTemplate,
      ObjectMapperCreator creator,
      RedisMessageProperties properties) {
    log.trace("[MESSAGE-REDIS] List Message Tempalte Factory");

    ObjectMapper objectMapper = createObjectMapper(creator);
    ListMessageTempalteFactory.instance.setJacksonRecord(new JacksonRecord(objectMapper));
    ListMessageTempalteFactory.instance.setStringRedisTemplate(stringRedisTemplate);
    ListMessageTempalteFactory.instance.setQueueOptionsMap(properties.getQueues());
    return ListMessageTempalteFactory.instance;
  }

  private ObjectMapper createObjectMapper(ObjectMapperCreator creator) {
    ObjectMapper objectMapper = creator.newInstance();
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("tutorials4j.framework.message.redis.bean")
            .build();
    objectMapper.activateDefaultTyping(
        ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    return objectMapper;
  }
}
