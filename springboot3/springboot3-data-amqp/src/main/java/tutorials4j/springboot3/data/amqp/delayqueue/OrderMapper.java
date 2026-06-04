package tutorials4j.springboot3.data.amqp.delayqueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class OrderMapper {
  private static int nextId = 1;
  private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

  public void insert(Order order) {
    order.setOrderId("order-" + nextId++);
    orderMap.put(order.getOrderId(), order);
  }

  public Order selectByOrderId(String orderId) {
    return orderMap.get(orderId);
  }

  public void updateById(Order order) {
    orderMap.put(order.getOrderId(), order);
  }

  public void print() {
    System.out.println("============");
    orderMap.forEach((k, v) -> System.out.println(v));
  }

  public List<Order> selectTimeoutOrder(long timeout) {
    return orderMap.values().stream()
        .filter(e -> e.getCreateTime() < timeout && e.getIsCanceled() == 0 && e.getPayStatus() == 0)
        .collect(Collectors.toList());
  }
}
