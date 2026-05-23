package tutorials4j.framework.examples.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "data_orders")
public class Order extends BaseEntity {
  private String orderNumber;
  private BigDecimal amount;

  @Column private LocalDateTime orderTime;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
}
