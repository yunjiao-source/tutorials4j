package tutorials4j.springboot3.data.amqp.orderedmessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 订单仓库
 *
 * @author Yun Jiao
 */
@Service
public class OrderRepository {
  private final Map<Long, String> orderMap = new ConcurrentHashMap<>();

  public String get(Long orderId) {
    String status = orderMap.get(orderId);
    if (status == null) {
      return "INIT";
    }
    return status;
  }

  public void put(Long orderId, String status) {
    orderMap.put(orderId, status);
  }

  public void countOrder() {
    System.out.println(">>>Total: " + orderMap.size());
    orderMap.values().stream()
        .filter(Objects::nonNull) // 忽略 null 值（可选）
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .forEach((str, count) -> System.out.println(str + " : " + count));
  }
}
