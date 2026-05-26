package tutorials4j.springboot3.integration.pay.strategy;

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
 * 支付策略接口
 *
 * @author Yun Jiao
 */
public interface PayStrategy {

  /** 获取支付渠道 */
  PayChannel getPayChannel();

  /** 统一支付 */
  PayResponse pay(PayRequest request);

  /** 查询订单 */
  QueryResponse queryOrder(PayRequest request);

  /** 关闭订单 */
  CloseResponse close(PayRequest request);

  /** 退款 */
  RefundResponse refund(RefundRequest request);

  /** 退款查询 */
  RefundResponse queryRefund(RefundRequest request);

  /** 处理回调 */
  NotifyResponse handleNotify(NotifyRequest request);
}
