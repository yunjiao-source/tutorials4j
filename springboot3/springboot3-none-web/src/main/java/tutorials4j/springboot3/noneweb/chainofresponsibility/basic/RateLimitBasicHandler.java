package tutorials4j.springboot3.noneweb.chainofresponsibility.basic;

import org.springframework.stereotype.Component;
import tutorials4j.springboot3.noneweb.chainofresponsibility.Request;

// 限流处理器
@Component
public class RateLimitBasicHandler extends AbstractBasicHandler {
  @Override
  public void handle(Request request) {
    System.out.println("[RateLimitHandler] 限流检查通过");
    doNext(request);
  }
}
