package tutorials4j.springboot3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.springboot3.model.PayChannel;
import tutorials4j.springboot3.request.PayRequest;
import tutorials4j.springboot3.request.RefundRequest;
import tutorials4j.springboot3.response.*;
import tutorials4j.springboot3.strategy.PayStrategy;
import tutorials4j.springboot3.strategy.PayStrategyFactory;

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
