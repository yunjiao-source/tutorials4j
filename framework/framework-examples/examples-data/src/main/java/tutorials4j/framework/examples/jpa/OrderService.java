package tutorials4j.framework.examples.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

/**
 * 订单业务服务。
 *
 * <p>提供订单的多条件组合分页查询能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class OrderService implements BaseService<Order, Long> {

  private final OrderRepository orderRepository;

  /**
   * 根据用户与订单条件组合查询订单并分页返回。
   *
   * <p>通过 {@link OrderSpecifications#buildSpecification} 构建动态查询条件， 并预拉取订单关联的用户数据以避免 N+1 查询。
   *
   * @param username 用户名（可选，模糊匹配）
   * @param email 邮箱（可选，精确匹配）
   * @param minAge 最小年龄（可选）
   * @param maxAge 最大年龄（可选）
   * @param minAmount 最小订单金额（可选）
   * @param orderStartTime 下单开始时间（可选）
   * @param orderEndTime 下单结束时间（可选）
   * @param pageable 分页参数
   * @return 符合条件的订单分页结果
   */
  public Page<Order> searchOrders(
      String username,
      String email,
      Integer minAge,
      Integer maxAge,
      BigDecimal minAmount,
      LocalDateTime orderStartTime,
      LocalDateTime orderEndTime,
      Pageable pageable) {

    // 构建 Specification（需要 fetch orders 时传 true）
    var spec =
        OrderSpecifications.buildSpecification(
            username,
            email,
            minAge,
            maxAge,
            minAmount,
            orderStartTime,
            orderEndTime,
            true // 拉取订单数据，避免 N+1
            );
    return orderRepository.findAll(spec, pageable);
  }

  /**
   * 返回订单数据仓库实例。
   *
   * @return 订单数据仓库
   */
  @Override
  public BaseRepository<Order, Long> getRepository() {
    return orderRepository;
  }
}
