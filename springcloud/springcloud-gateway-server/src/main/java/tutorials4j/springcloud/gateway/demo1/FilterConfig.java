package tutorials4j.springcloud.gateway.demo1;

import java.util.Map;
import lombok.Data;

//
@Data
public class FilterConfig {
  private String name;
  private Map<String, String> args;
}
