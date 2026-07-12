package tutorials4j.framework.examples.message.redis.integration.pub;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequestMapping("/demo")
@RestController
public class DemoController {
  //  private static final AtomicLong id = new AtomicLong();
  //  private final RedisMessageTemplate demoStreamHandler;
  //  private final JacksonRecord jacksonRecord;
  //
  //  public DemoController(RedisMessageTemplateFactoryResolver resolver, ObjectMapperCreator
  // creator) {
  //    this.demoStreamHandler =
  // resolver.factory(RedisMessageType.stream).handler(Consts.MESSAGE_KEY);
  //    this.jacksonRecord = new JacksonRecord(creator.getInstance());
  //  }
  //
  //  @GetMapping("send")
  //  public String send() {
  //    IntStream.range(0, 5)
  //        .forEach(
  //            (i) -> {
  //              DemoData data = new DemoData("" + id.incrementAndGet());
  //
  //              RedisMessage message =
  //                  RedisMessage.defaultValue().setData(jacksonRecord.toJson(data));
  //              demoStreamHandler.send(message);
  //            });
  //    return "ok";
  //  }
}
