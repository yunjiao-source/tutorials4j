package tutorials4j.springboot3.integration.pay;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.integration.pay.model.PayChannel;
import tutorials4j.springboot3.integration.pay.model.TradeType;
import tutorials4j.springboot3.integration.pay.request.PayRequest;
import tutorials4j.springboot3.integration.pay.response.PayResponse;
import tutorials4j.springboot3.integration.pay.service.PayService;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {
  private final PayService payService;

  @Override
  public void run(String... args) throws Exception {
    // 创建支付请求
    PayRequest request = new PayRequest();

    // 公共参数
    request.setAppId("wxd678efxxxxxxxxxxxxxxxxx");
    request.setMchId("123xxxxxxxx");
    request.setSerialNo("1DDE55AD98Exxxxxxxxxx");
    request.setPrivateKey("XXXXXXXXXXXXXXXXXXXXXXXXX");
    request.setPublicKey("XXXXXXXXXXXXXXXXXXXXXXXXXX");
    request.setApiV3Key("XXXXXXXXXXXXXXXXXXXXXX");

    // 订单参数
    request.setPayChannel(PayChannel.WECHAT_PAY);
    request.setTradeType(TradeType.APP);
    request.setOrderNo("ORDER_" + System.currentTimeMillis());
    request.setAmount(new BigDecimal("100")); // 1元
    request.setDescription("测试商品");
    request.setClientIp("127.0.0.1");

    // 发起支付
    PayResponse response = payService.pay(request);

    if (response.isSuccess()) {
      // 支付创建成功，返回支付参数给前端
      System.out.println("支付参数：" + response.getPayParams());
    } else {
      // 处理支付失败
      System.out.println("支付失败：" + response.getErrorMsg());
    }
  }
}
