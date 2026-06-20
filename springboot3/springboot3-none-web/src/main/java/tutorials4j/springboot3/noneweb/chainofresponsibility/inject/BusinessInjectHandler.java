package tutorials4j.springboot3.noneweb.chainofresponsibility.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.noneweb.chainofresponsibility.RequestContext;

@Order(3)
@Component
public class BusinessInjectHandler implements InjectHandler {
  @Override
  public void handle(RequestContext context) {
    System.out.println("[BusinessHandler] 处理业务: " + context.getRequest().getData());
  }
}
