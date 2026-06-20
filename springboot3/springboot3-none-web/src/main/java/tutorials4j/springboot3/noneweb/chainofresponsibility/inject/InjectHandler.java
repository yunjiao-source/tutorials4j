package tutorials4j.springboot3.noneweb.chainofresponsibility.inject;

import tutorials4j.springboot3.noneweb.chainofresponsibility.RequestContext;

// 定义处理器接口
public interface InjectHandler {
  void handle(RequestContext request);
}
