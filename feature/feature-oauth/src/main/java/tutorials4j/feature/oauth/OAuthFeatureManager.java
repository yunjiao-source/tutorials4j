package tutorials4j.feature.oauth;

import java.util.function.Consumer;
import tutorials4j.feature.oauth.entity.ClientEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public final class OAuthFeatureManager {
  private OAuthFeatureManager() {}

  private static class Holder {
    private static final OAuthFeatureManager INSTANCE = new OAuthFeatureManager();
  }

  public static OAuthFeatureManager getInstance() {
    return OAuthFeatureManager.Holder.INSTANCE;
  }

  public Consumer<ClientEntity> setClientEntityDefaultValue = (clientEntity) -> {};
}
