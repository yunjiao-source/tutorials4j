package tutorials4j.springboot3.noneweb.chainofresponsibility.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.noneweb.chainofresponsibility.RequestContext;

@Order(2)
@Component
public class RateLimitIInjectHandler implements InjectHandler {
  @Override
  public void handle(RequestContext request) {
    System.out.println("[RateLimitHandler] 限流检查通过");
  }
}
