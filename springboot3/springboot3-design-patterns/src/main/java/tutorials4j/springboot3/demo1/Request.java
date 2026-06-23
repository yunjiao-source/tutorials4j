package tutorials4j.springboot3.demo1;

import lombok.Data;

// 请求实体
@Data
public class Request {
  private String token;
  private String data;
}
