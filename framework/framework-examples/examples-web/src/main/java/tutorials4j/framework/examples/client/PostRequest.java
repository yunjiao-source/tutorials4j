package tutorials4j.framework.examples.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求实体
 *
 * @author Yun Jiao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
  private String title;
  private String body;
  private Integer userId;
}
