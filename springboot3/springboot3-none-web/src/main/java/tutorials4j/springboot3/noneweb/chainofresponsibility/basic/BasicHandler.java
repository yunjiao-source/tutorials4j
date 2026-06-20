package tutorials4j.springboot3.noneweb.chainofresponsibility.basic;

import tutorials4j.springboot3.noneweb.chainofresponsibility.Request;

// 抽象处理器接口
public interface BasicHandler {
  void handle(Request request);
}
