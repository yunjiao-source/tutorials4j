package tutorials4j.feature.upms.component;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.feature.upms.autoconfigure.UpmsFeatureProperties;
import tutorials4j.feature.upms.service.AuthHistoryService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobComponent {
  private final UpmsFeatureProperties properties;
  private final AuthHistoryService authHistoryService;

  @XxlJob("clearAuthHistoryExpiredData")
  public void clearAuthHistoryExpiredData() {
    if (log.isDebugEnabled()) {
      log.debug("执行XxlJob任务：{}", properties.getClearAuthHistory());
    }
    int loopCount = properties.getClearAuthHistory().getMaxLoopCount();
    for (int i = 0; i <= loopCount; i++) {
      int rows = authHistoryService.clearExpireData();
      if (rows <= 0) {
        break;
      }
    }
  }
}
