package tutorials4j.springboot3.integration.pay.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.integration.pay.model.PayChannel;
import tutorials4j.springboot3.integration.pay.request.PayRequest;
import tutorials4j.springboot3.integration.pay.request.RefundRequest;
import tutorials4j.springboot3.integration.pay.response.CloseResponse;
import tutorials4j.springboot3.integration.pay.response.NotifyRequest;
import tutorials4j.springboot3.integration.pay.response.NotifyResponse;
import tutorials4j.springboot3.integration.pay.response.PayResponse;
import tutorials4j.springboot3.integration.pay.response.QueryResponse;
import tutorials4j.springboot3.integration.pay.response.RefundResponse;

/**
 * 银联支付实现
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class UnionPayStrategy extends AbstractPayStrategy {

  @Override
  protected PayResponse doPay(PayRequest request) {
    try {
      // TODO 业务实现
      return null;
    } catch (Exception e) {
      throw new RuntimeException("银联支付失败", e);
    }
  }

  @Override
  protected QueryResponse doQueryOrder(PayRequest request) {
    return null;
  }

  @Override
  protected CloseResponse doClose(PayRequest request) {
    return null;
  }

  @Override
  protected RefundResponse doRefund(RefundRequest request) {
    return null;
  }

  @Override
  protected RefundResponse doQueryRefund(RefundRequest request) {
    return null;
  }

  @Override
  public PayChannel getPayChannel() {
    return PayChannel.UNION_PAY;
  }

  @Override
  public NotifyResponse handleNotify(NotifyRequest request) {
    return null;
  }
}
