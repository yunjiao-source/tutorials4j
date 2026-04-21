package tutorials4j.framework.examples;

import lombok.Data;

/**
 * 响应实体
 *
 * @author Yun Jiao
 */
@Data
public class PostResponse {
    private Integer id;
    private String title;
    private String body;
    private Integer userId;
}
