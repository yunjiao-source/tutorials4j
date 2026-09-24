package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Slf4j
public class CompositeSaFilterAuthStrategy implements SaFilterAuthStrategy {
  private final List<NamedSaFilterAuthStrategy> strategies;

  public CompositeSaFilterAuthStrategy(
      Collection<? extends NamedSaFilterAuthStrategy> pointcutSaFilterAuthStrategies) {
    Assert.notNull(
        pointcutSaFilterAuthStrategies, "pointcutSaFilterAuthStrategies must not be null");

    this.strategies = new ArrayList<>(pointcutSaFilterAuthStrategies);
  }

  public CompositeSaFilterAuthStrategy newInstanceBy(List<String> authStrategyNames) {
    Assert.notNull(authStrategyNames, "authStrategyNames must not be null");

    var newStrategies = new ArrayList<NamedSaFilterAuthStrategy>();
    authStrategyNames.forEach(
        name -> {
          for (NamedSaFilterAuthStrategy strategy : this.strategies) {
            if (Objects.equals(strategy.getName(), name)) {
              newStrategies.add(strategy);
              break;
            }
          }
        });
    return new CompositeSaFilterAuthStrategy(newStrategies);
  }

  @Override
  public void run(Object o) {
    strategies.forEach(
        e -> {
          if (log.isDebugEnabled()) {
            log.debug("执行SaFilterAuthStrategy：{}", e.getClass().getSimpleName());
          }
          e.run(o);
        });
  }
}
