package tutorials4j.feature.oauth.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.autoconfigure.OAuthFeatureProperties;
import tutorials4j.feature.oauth.service.AuthHistoryService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ClearExpiredDataComponent {
  private final OAuthFeatureProperties properties;
  private final AuthHistoryService authHistoryService;

  public void clearAuthHistoryExpiredData() {
    int loopCount = properties.getClearAuthHistory().getMaxLoopCount();
    for (int i = 0; i <= loopCount; i++) {
      int rows = authHistoryService.clearExpireData();
      if (rows <= 0) {
        break;
      }
    }
  }
}
