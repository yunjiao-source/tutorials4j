package tutorials4j.springboot3.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.model.*;
import tutorials4j.springboot3.request.PayRequest;
import tutorials4j.springboot3.request.RefundRequest;
import tutorials4j.springboot3.response.*;

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
