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
@Slf4j
public class CompositeFilterAuthStrategy implements SaFilterAuthStrategy {
  @Getter private final List<PointcutSaFilterAuthStrategy> strategies;
  private AuthFilterPointcutEnum pointcut;

  public CompositeFilterAuthStrategy(
      Collection<? extends PointcutSaFilterAuthStrategy> pointcutSaFilterAuthStrategies) {
    Assert.notNull(
        pointcutSaFilterAuthStrategies, "pointcutSaFilterAuthStrategies must not be null");

    this.strategies = new ArrayList<>(pointcutSaFilterAuthStrategies);
  }

  public CompositeFilterAuthStrategy copyAndSetPointcut(
      AuthFilterPointcutEnum authFilterPointcutEnum) {
    Assert.notNull(authFilterPointcutEnum, "authFilterPointcutEnum must not be null");

    var copyInstant = new CompositeFilterAuthStrategy(this.strategies);
    copyInstant.pointcut = authFilterPointcutEnum;
    return copyInstant;
  }

  @Override
  public void run(Object o) {
    if (pointcut == null) {
      log.warn("pointcut属性是null，请确认配置是否正确");
    }

    strategies.stream()
        .filter(e -> Objects.equals(this.pointcut, e.getPointcut()))
        .forEach(
            e -> {
              if (log.isDebugEnabled()) {
                log.debug("执行[{}]认证策略：{}", pointcut.name(), e.getClass().getName());
              }
              e.run(o);
            });
  }
}
