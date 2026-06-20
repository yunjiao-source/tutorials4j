package tutorials4j.springboot3.noneweb.chainofresponsibility;

import lombok.Data;

// 请求实体
@Data
public class Request {
  private String token;
  private String data;
}
