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

/**
 * 订单实体，对应数据表 data_orders。
 *
 * <p>订单与用户为多对一关系，保存订单号、金额、下单时间等字段，用于演示 JPA 关联映射。
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(name = "data_orders")
public class Order extends BaseEntity {
  /** 订单号 */
  private String orderNumber;

  /** 订单金额 */
  private BigDecimal amount;

  /** 下单时间 */
  @Column private LocalDateTime orderTime;

  /** 下单用户 */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
}
