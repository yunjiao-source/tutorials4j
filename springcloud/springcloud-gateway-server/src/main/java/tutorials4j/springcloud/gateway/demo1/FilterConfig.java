package tutorials4j.springcloud.gateway.demo1;

import java.util.Map;
import lombok.Data;

//
/**
 * 网关过滤器配置模型，描述单个过滤器的名称及参数键值对。
 *
 * @author Yun Jiao
 */
@Data
public class FilterConfig {
  /** 过滤器名称。 */
  private String name;

  /** 过滤器参数键值对。 */
  private Map<String, String> args;
}
