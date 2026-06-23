package tutorials4j.springboot3.demo1.inject;

import tutorials4j.springboot3.demo1.RequestContext;

// 定义处理器接口
public interface InjectHandler {
  void handle(RequestContext request);
}
