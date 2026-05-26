package tutorials4j.springboot3.integration.pay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.springboot3.integration.pay.model.PayChannel;
import tutorials4j.springboot3.integration.pay.request.PayRequest;
import tutorials4j.springboot3.integration.pay.request.RefundRequest;
import tutorials4j.springboot3.integration.pay.response.CloseResponse;
import tutorials4j.springboot3.integration.pay.response.NotifyRequest;
import tutorials4j.springboot3.integration.pay.response.NotifyResponse;
import tutorials4j.springboot3.integration.pay.response.PayResponse;
import tutorials4j.springboot3.integration.pay.response.QueryResponse;
import tutorials4j.springboot3.integration.pay.response.RefundResponse;
import tutorials4j.springboot3.integration.pay.strategy.PayStrategy;
import tutorials4j.springboot3.integration.pay.strategy.PayStrategyFactory;

/**
 * 统一支付服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

  private final PayStrategyFactory payStrategyFactory;

  /**
   * @Description： 支付 @Date： 2025/11/21 @Param： [request]
   */
  public PayResponse pay(PayRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(request.getPayChannel());
    return strategy.pay(request);
  }

  /**
   * @Description： 查询订单 @Date： 2025/11/21 @Param： [request]
   */
  public QueryResponse queryOrder(PayRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(request.getPayChannel());
    return strategy.queryOrder(request);
  }

  /**
   * @Description： 关闭订单 @Date： 2025/11/21 @Param： [request]
   */
  public CloseResponse close(PayRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(request.getPayChannel());
    return strategy.close(request);
  }

  /**
   * @Description： 退款 @Date： 2025/11/21 @Param： [payChannel, request]
   */
  public RefundResponse refund(PayChannel payChannel, RefundRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(payChannel);
    return strategy.refund(request);
  }

  /**
   * @Description： 退款查询 @Date： 2025/11/21 @Param： [payChannel, request]
   */
  public RefundResponse queryRefund(PayChannel payChannel, RefundRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(payChannel);
    return strategy.queryRefund(request);
  }

  /**
   * @Description： 处理回调 @Date： 2025/11/21 @Param： [request]
   */
  public NotifyResponse handleNotify(NotifyRequest request) {
    PayStrategy strategy = payStrategyFactory.getStrategy(request.getPayChannel());
    return strategy.handleNotify(request);
  }
}
