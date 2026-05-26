package tutorials4j.springboot3.data.mongodb.simple;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 评论实体（嵌套文档）
 *
 * @author Yun Jiao
 */
@Data
@NoArgsConstructor
public class Comment {

  private String content;
  private String author;

  @Field("created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
}
