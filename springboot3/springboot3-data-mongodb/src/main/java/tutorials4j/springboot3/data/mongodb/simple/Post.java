package tutorials4j.springboot3.data.mongodb.simple;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 文章实体
 *
 * @author Yun Jiao
 */
@Data
@NoArgsConstructor
@Document(collection = "posts")
public class Post {

  @Id private String id;

  private String title;
  private String content;

  @Field("author")
  private String author;

  private List<Comment> comments = new ArrayList<>();

  @Field("created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Field("updated_at")
  private LocalDateTime updatedAt;
}
