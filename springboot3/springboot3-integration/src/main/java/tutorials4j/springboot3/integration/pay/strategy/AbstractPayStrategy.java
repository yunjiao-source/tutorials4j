package tutorials4j.springboot3.integration.pay.strategy;

import lombok.extern.slf4j.Slf4j;
import tutorials4j.springboot3.integration.pay.model.Constants;
import tutorials4j.springboot3.integration.pay.request.PayRequest;
import tutorials4j.springboot3.integration.pay.request.RefundRequest;
import tutorials4j.springboot3.integration.pay.response.CloseResponse;
import tutorials4j.springboot3.integration.pay.response.PayResponse;
import tutorials4j.springboot3.integration.pay.response.QueryResponse;
import tutorials4j.springboot3.integration.pay.response.RefundResponse;

/**
 * 支付策略实现
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractPayStrategy implements PayStrategy {

  @Override
  public PayResponse pay(PayRequest request) {
    try {
      log.info("开始支付，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo());
      return doPay(request);
    } catch (Exception e) {
      log.error("支付异常，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo(), e);
      return PayResponse.failure(Constants.OPERATE_ERROR_MSG, "支付处理异常：" + e.getMessage());
    }
  }

  @Override
  public QueryResponse queryOrder(PayRequest request) {
    try {
      log.info("查询订单，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo());
      return doQueryOrder(request);
    } catch (Exception e) {
      log.error("查询订单异常，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo(), e);
      return QueryResponse.failure(Constants.OPERATE_ERROR_MSG, "查询订单异常：" + e.getMessage());
    }
  }

  @Override
  public CloseResponse close(PayRequest request) {
    try {
      log.info("关闭订单，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo());
      return doClose(request);
    } catch (Exception e) {
      log.error("关闭订单异常，渠道：{}，订单号：{}", getPayChannel().getName(), request.getOrderNo(), e);
      return CloseResponse.failure(Constants.OPERATE_ERROR_MSG, "关闭订单异常：" + e.getMessage());
    }
  }

  @Override
  public RefundResponse refund(RefundRequest request) {
    try {
      log.info("开始退款，渠道：{}，退款单号：{}", getPayChannel().getName(), request.getRefundNo());
      return doRefund(request);
    } catch (Exception e) {
      log.error("退款异常，渠道：{}，退款单号：{}", getPayChannel().getName(), request.getRefundNo(), e);
      return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, "退款处理异常：" + e.getMessage());
    }
  }

  @Override
  public RefundResponse queryRefund(RefundRequest request) {
    try {
      log.info("开始退款查询，渠道：{}，退款单号：{}", getPayChannel().getName(), request.getRefundNo());
      return doQueryRefund(request);
    } catch (Exception e) {
      log.error("退款查询异常，渠道：{}，退款单号：{}", getPayChannel().getName(), request.getRefundNo(), e);
      return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, "退款查询处理异常：" + e.getMessage());
    }
  }

  protected abstract PayResponse doPay(PayRequest request);

  protected abstract QueryResponse doQueryOrder(PayRequest request);

  protected abstract CloseResponse doClose(PayRequest request);

  protected abstract RefundResponse doRefund(RefundRequest request);

  protected abstract RefundResponse doQueryRefund(RefundRequest request);
}
