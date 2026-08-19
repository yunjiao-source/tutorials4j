package tutorials4j.framework.examples.client;

import lombok.Data;

/**
 * 帖子（Post）数据模型：对应 jsonplaceholder 接口返回的帖子数据结构。
 *
 * @author Yun Jiao
 */
@Data
public class Post {

  /** 用户编号 */
  private Integer userId;

  /** 帖子编号 */
  private Integer id;

  /** 帖子标题 */
  private String title;

  /** 帖子正文 */
  private String body;
}
