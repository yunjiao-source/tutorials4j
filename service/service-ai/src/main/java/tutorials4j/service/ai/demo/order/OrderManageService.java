package tutorials4j.service.ai.demo.order;

import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class OrderManageService {
  public String getOrderById(String orderId) {
    return "订单号：" + orderId;
  }

  public String refund(String orderId, String reason) {
    System.out.println("退款成功");
    return "退款单号：" + UUID.randomUUID().toString();
  }
}
