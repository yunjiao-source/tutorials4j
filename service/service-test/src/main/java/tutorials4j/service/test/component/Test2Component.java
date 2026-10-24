package tutorials4j.service.test.component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tutorials4j.feature.basic.api.BasicApiClient;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.feature.upms.api.UpmsApiClient;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class Test2Component {
  private final UpmsApiClient upmsApiClient;
  private final BasicApiClient basicApiClient;

  @Scheduled(initialDelay = 5000, fixedRate = 5000)
  void getByUsername() {
    String username = "admin";
    if (hit40()) {
      // 无效的
      username += "1";
    }
    System.out.println("===" + upmsApiClient.getByUsername(username));
  }

  @Scheduled(initialDelay = 7000, fixedRate = 7000)
  void getDictDataTreeByCode() {
    List<DictDataVO> data = basicApiClient.getDictDataByCode("xingzqy", null);
    System.out.println("===" + data);
  }

  private boolean hit40() {
    return ThreadLocalRandom.current().nextInt(100) < 40;
  }
}
