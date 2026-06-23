package tutorials4j.springboot3.demo1;

import lombok.Data;

@Data
public class RequestContext {
  private Request request;
  private Throwable throwable;

  public RequestContext() {}

  public RequestContext(Request request) {
    this.request = request;
  }
}
