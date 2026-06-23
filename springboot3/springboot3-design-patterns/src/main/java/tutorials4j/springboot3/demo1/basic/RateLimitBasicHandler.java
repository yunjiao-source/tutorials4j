package tutorials4j.springboot3.demo1.basic;

import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.Request;

// 限流处理器
@Component
public class RateLimitBasicHandler extends AbstractBasicHandler {
  @Override
  public void handle(Request request) {
    System.out.println("[RateLimitHandler] 限流检查通过");
    doNext(request);
  }
}
