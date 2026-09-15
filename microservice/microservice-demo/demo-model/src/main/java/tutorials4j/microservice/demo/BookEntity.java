package tutorials4j.microservice.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "demo_book")
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity extends BaseEntity<String> {
  /** 书名，非空，长度 100 */
  @Column(nullable = false, length = 100)
  private String title;

  /** 作者，非空，长度 50 */
  @Column(nullable = false, length = 50)
  private String author;

  /** 价格 */
  @Column(precision = 10, scale = 2)
  private BigDecimal price;

  @Column private LocalDate publishDate;
}
