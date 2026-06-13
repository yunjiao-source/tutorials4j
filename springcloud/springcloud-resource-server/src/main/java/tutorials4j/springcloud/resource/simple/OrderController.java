package tutorials4j.springcloud.resource.simple;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  @GetMapping("/api/orders/{orderId}")
  @PreAuthorize(
      "hasAuthority('SCOPE_order.read') and @tenantPermissionEvaluator.canAccessOrder(authentication, #orderId)")
  public String detail(@PathVariable Long orderId) {
    return "order-" + orderId;
  }
}
