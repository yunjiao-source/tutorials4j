package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.router.SaRouter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class WhiteUrlsSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {
  private final List<String> whiteUrls;

  @Override
  public void run(Object obj) {
    SaRouter.match(whiteUrls).stop();
  }

  public String getName() {
    return SaTokenUtils.WHITE_URL_AUTH_STRATEGY;
  }
}
