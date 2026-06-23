package tutorials4j.springboot3.demo1.basic;

import tutorials4j.springboot3.demo1.Request;

// 抽象处理器接口
public interface BasicHandler {
  void handle(Request request);
}
