package tutorials4j.service.ai.demo.order;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class OrderTools {
  private final OrderManageService orderManageService;

  @Tool(name = "apply_refund", description = "根据用户传入的订单信息发起退款")
  public String refund(
      @ToolParam(description = "订单编号，为数字类型") String orderId,
      @ToolParam(description = "商品名称") String name,
      @ToolParam(description = "退款原因") String reason) {
    System.out.println("已为商品:" + name + ",订单号:" + orderId + "申请退款 , 退款原因： " + reason);

    orderManageService.refund(orderId, reason);

    return "已为商品：" + name + ",订单号：" + orderId + "申请退款 , 退款原因： " + reason;
  }
}
