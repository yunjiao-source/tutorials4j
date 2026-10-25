package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.router.SaRouter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import tutorials4j.toolkit.satoken.exception.BlockUrlException;
import tutorials4j.toolkit.satoken.util.SaTokenToolkitUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class BlockUrlsSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {
  private final List<String> blockUrls;

  @Override
  public void run(Object obj) {
    SaRouter.match(blockUrls)
        .check(
            () -> {
              throw new BlockUrlException(SaHolder.getRequest().getRequestPath());
            });
  }

  @Override
  public String getName() {
    return SaTokenToolkitUtils.BLOCK_URLS_AUTH_STRATEGY;
  }
}
