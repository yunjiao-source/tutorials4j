package tutorials4j.toolkit.satoken.strategy;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;
import tutorials4j.toolkit.satoken.util.SaTokenToolkitUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class CorsSaFilterAuthStrategy implements NamedSaFilterAuthStrategy {

  @Override
  public void run(Object obj) {
    SaResponse response = SaHolder.getResponse();
    response
        .setHeader("Access-Control-Allow-Origin", "*")
        .setHeader("Access-Control-Allow-Methods", "*")
        .setHeader("Access-Control-Allow-Headers", "*")
        .setHeader("Access-Control-Max-Age", "3600");
  }

  @Override
  public String getName() {
    return SaTokenToolkitUtils.CORS_AUTH_STRATEGY;
  }
}
