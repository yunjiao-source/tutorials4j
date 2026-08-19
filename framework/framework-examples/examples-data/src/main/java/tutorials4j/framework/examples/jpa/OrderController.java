package tutorials4j.framework.examples.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单查询接口控制器。
 *
 * <p>提供按用户信息及订单信息组合条件分页查询订单的 REST 接口。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  /**
   * 按条件分页查询订单。
   *
   * @param username 用户名（可选，模糊匹配）
   * @param email 邮箱（可选，精确匹配）
   * @param minAge 最小年龄（可选）
   * @param maxAge 最大年龄（可选）
   * @param minAmount 最小订单金额（可选）
   * @param orderStartTime 下单开始时间（可选）
   * @param orderEndTime 下单结束时间（可选）
   * @param pageable 分页参数，默认按创建时间倒序、每页 10 条
   * @return 订单分页结果
   */
  @GetMapping
  public PagedModel<OrderVO> searchUsers(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) Integer minAge,
      @RequestParam(required = false) Integer maxAge,
      @RequestParam(required = false) BigDecimal minAmount,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime orderStartTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime orderEndTime,
      @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
          Pageable pageable) {

    Page<Order> orders =
        orderService.searchOrders(
            username, email, minAge, maxAge, minAmount, orderStartTime, orderEndTime, pageable);
    return new PagedModel<>(orders.map(OrderVO::of));
  }
}
