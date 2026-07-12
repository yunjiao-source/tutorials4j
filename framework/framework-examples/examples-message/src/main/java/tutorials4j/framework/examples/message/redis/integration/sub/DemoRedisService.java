package tutorials4j.framework.examples.message.redis.integration.sub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class DemoRedisService {
  //  private final RedisMessageTemplate demoStreamHandler;
  //  private final RedisMessageTemplate demoZSetHandler;
  //  private final DemoRedisConsumer demoRedisConsumer;
  //
  //  public DemoRedisService(
  //      DemoService demoService,
  //      RedisMessageTemplateFactoryResolver resolver,
  //      ObjectMapperCreator creator) {
  //    this.demoStreamHandler =
  // resolver.factory(RedisMessageType.stream).handler(Consts.MESSAGE_KEY);
  //    this.demoZSetHandler = resolver.factory(RedisMessageType.zset).handler(Consts.MESSAGE_KEY);
  //
  //    JacksonRecord jacksonRecord = new JacksonRecord(creator.getInstance());
  //    demoRedisConsumer =
  //        new DemoRedisConsumer(demoService, jacksonRecord, demoStreamHandler, demoZSetHandler);
  //  }
  //
  //  public void init() {
  //    demoRedisConsumer.start();
  //  }
  //
  //  @PreDestroy
  //  public void destroy() {
  //    log.info("服务关闭");
  //    demoStreamHandler.shutdown();
  //    demoZSetHandler.shutdown();
  //  }
  //
  //  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  //  public void clean() {
  //    if (demoStreamHandler instanceof StreamMessageTemplate streamMessageTemplate) {
  //      long count = streamMessageTemplate.trimByMinId();
  //      if (count > 0) {
  //        log.info("清理了{}条记录", count);
  //      }
  //      return;
  //    }
  //
  //    log.error("{}不是流消息处理器，无法执行流清理方法", demoStreamHandler.getClass().getSimpleName());
  //  }
}
