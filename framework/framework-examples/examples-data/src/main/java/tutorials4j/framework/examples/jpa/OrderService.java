package tutorials4j.framework.examples.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

@Service
@RequiredArgsConstructor
public class OrderService implements BaseService<Order, Long> {

  private final OrderRepository orderRepository;

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

  @Override
  public BaseRepository<Order, Long> getRepository() {
    return orderRepository;
  }
}
