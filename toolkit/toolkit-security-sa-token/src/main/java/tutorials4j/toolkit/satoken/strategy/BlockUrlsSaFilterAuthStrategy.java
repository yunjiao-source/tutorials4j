package tutorials4j.toolkit.satoken.strategy;

import static tutorials4j.toolkit.satoken.strategy.StrageyOrderd.BLOCK_URL;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.router.SaRouter;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class BlockUrlsSaFilterAuthStrategy implements PointcutSaFilterAuthStrategy {
  private final List<String> blockUrls;

  @Override
  public void run(Object obj) {
    SaRouter.match(blockUrls).back("禁止访问：" + SaHolder.getRequest().getRequestPath());
  }

  @Override
  public int getOrder() {
    return BLOCK_URL;
  }
}
