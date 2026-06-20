package tutorials4j.springboot3.noneweb.chainofresponsibility.basic;

import org.springframework.stereotype.Component;
import tutorials4j.springboot3.noneweb.chainofresponsibility.Request;

@Component
public class BasicHandlerChain {
  private final AbstractBasicHandler chain;

  // 手动编排处理器顺序
  public BasicHandlerChain(
      AuthBasicHandler auth, RateLimitBasicHandler rateLimit, BusinessBasicHandler business) {
    auth.setNext(rateLimit);
    rateLimit.setNext(business);
    this.chain = auth;
  }

  public void execute(Request request) {
    chain.handle(request);
  }
}
