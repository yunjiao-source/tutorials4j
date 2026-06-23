package tutorials4j.springboot3.demo1.basic;

import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.Request;

// 业务处理器
@Component
public class BusinessBasicHandler extends AbstractBasicHandler {
  @Override
  public void handle(Request request) {
    System.out.println("[BusinessHandler] 开始处理业务: " + request.getData());
  }
}
