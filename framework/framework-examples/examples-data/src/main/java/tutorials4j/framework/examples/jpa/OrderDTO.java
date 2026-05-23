package tutorials4j.framework.examples.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseDTO;
import tutorials4j.framework.examples.SexEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class OrderDTO extends BaseDTO {
  private String orderNumber;
  private BigDecimal amount;
  private LocalDateTime orderTime;
  private Long userId;

  private String email;
  private SexEnum sex;

  public static OrderDTO of(Order order) {
    OrderDTO dto = new OrderDTO();

    User user = order.getUser();
    if (user != null) {
      BeanUtils.copyProperties(user, dto);
      dto.setUserId(user.getId());
    }

    BeanUtils.copyProperties(order, dto);
    return dto;
  }

  public static List<OrderDTO> of(List<Order> orders) {
    List<OrderDTO> dtoList = new ArrayList<>();
    for (Order order : orders) {
      dtoList.add(of(order));
    }

    return dtoList;
  }
}
