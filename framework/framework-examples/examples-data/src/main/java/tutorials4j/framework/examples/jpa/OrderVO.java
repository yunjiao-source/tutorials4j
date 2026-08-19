package tutorials4j.framework.examples.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseVO;
import tutorials4j.framework.examples.SexEnum;

/**
 * 订单视图对象。
 *
 * <p>用于向调用方返回订单及其关联用户的展示信息。
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class OrderVO extends BaseVO {
  private String orderNumber;
  private BigDecimal amount;
  private LocalDateTime orderTime;
  private Long userId;

  private String email;
  private SexEnum sex;

  /**
   * 将订单实体转换为视图对象。
   *
   * @param order 订单实体
   * @return 订单视图对象
   */
  public static OrderVO of(Order order) {
    OrderVO dto = new OrderVO();

    User user = order.getUser();
    if (user != null) {
      BeanUtils.copyProperties(user, dto);
      dto.setUserId(user.getId());
    }

    BeanUtils.copyProperties(order, dto);
    return dto;
  }

  /**
   * 将订单实体列表批量转换为视图对象列表。
   *
   * @param orders 订单实体列表
   * @return 订单视图对象列表
   */
  public static List<OrderVO> of(List<Order> orders) {
    List<OrderVO> dtoList = new ArrayList<>();
    for (Order order : orders) {
      dtoList.add(of(order));
    }

    return dtoList;
  }
}
