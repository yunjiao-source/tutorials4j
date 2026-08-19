package tutorials4j.springcloud.resource.simple;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单资源控制器：提供受保护订单数据的查询接口，演示资源服务器的权限控制。
 *
 * @author Yun Jiao
 */
@RestController
public class OrderController {

  /**
   * 根据订单 ID 查询订单详情，需要 SCOPE_order.read 权限并通过租户权限评估器校验订单归属。
   *
   * @param orderId 订单 ID
   * @return 订单信息字符串
   */
  @GetMapping("/api/orders/{orderId}")
  @PreAuthorize(
      "hasAuthority('SCOPE_order.read') and @tenantPermissionEvaluator.canAccessOrder(authentication, #orderId)")
  public String detail(@PathVariable Long orderId) {
    return "order-" + orderId;
  }
}
