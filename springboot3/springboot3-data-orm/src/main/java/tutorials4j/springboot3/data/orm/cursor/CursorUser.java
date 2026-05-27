package tutorials4j.springboot3.data.orm.cursor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "t_cursor_user")
public class CursorUser {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;
  private LocalDateTime createdAt;
}
