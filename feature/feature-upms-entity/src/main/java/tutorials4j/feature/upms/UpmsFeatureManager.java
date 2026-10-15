package tutorials4j.feature.upms;

import java.util.function.Consumer;
import tutorials4j.feature.upms.entity.ClientEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public final class UpmsFeatureManager {
  private UpmsFeatureManager() {}

  private static class Holder {
    private static final UpmsFeatureManager INSTANCE = new UpmsFeatureManager();
  }

  public static UpmsFeatureManager getInstance() {
    return UpmsFeatureManager.Holder.INSTANCE;
  }

  public Consumer<ClientEntity> setClientEntityDefaultValue = (clientEntity) -> {};
}
