package tutorials4j.springboot3.strategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.model.PayChannel;

/**
 * 支付策略工厂
 *
 * @author Yun Jiao
 */
@Component
public class PayStrategyFactory {

  private final Map<PayChannel, PayStrategy> strategyMap = new ConcurrentHashMap<>();

  public PayStrategyFactory(List<PayStrategy> strategies) {
    for (PayStrategy strategy : strategies) {
      strategyMap.put(strategy.getPayChannel(), strategy);
    }
  }

  public PayStrategy getStrategy(PayChannel payChannel) {
    PayStrategy strategy = strategyMap.get(payChannel);
    if (strategy == null) {
      throw new RuntimeException("不支持的支付渠道：" + payChannel.getName());
    }
    return strategy;
  }

  public PayStrategy getStrategy(String channelCode) {
    PayChannel payChannel = PayChannel.getByCode(channelCode);
    if (payChannel == null) {
      throw new RuntimeException("不支持的支付渠道代码：" + channelCode);
    }
    return getStrategy(payChannel);
  }
}
