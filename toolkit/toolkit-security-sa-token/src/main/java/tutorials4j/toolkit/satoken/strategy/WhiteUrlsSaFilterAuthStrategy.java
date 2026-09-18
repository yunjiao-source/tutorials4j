package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrategyOrderd.AUTH_WHITE_URL;

import cn.dev33.satoken.router.SaRouter;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class WhiteUrlsSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {
  private final List<String> whiteUrls;

  @Override
  public int getOrder() {
    return AUTH_WHITE_URL;
  }

  @Override
  public void run(Object obj) {
    SaRouter.match(whiteUrls).stop();
  }
}
