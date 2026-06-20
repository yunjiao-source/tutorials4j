package tutorials4j.springboot3.noneweb.chainofresponsibility.basic;

import tutorials4j.springboot3.noneweb.chainofresponsibility.Request;

// 抽象基类（维护next引用）
public abstract class AbstractBasicHandler implements BasicHandler {
  protected AbstractBasicHandler next;

  // 设置下一个处理器
  public void setNext(AbstractBasicHandler next) {
    this.next = next;
  }

  // 向下传递请求
  protected void doNext(Request request) {
    if (next != null) {
      next.handle(request);
    }
  }
}
