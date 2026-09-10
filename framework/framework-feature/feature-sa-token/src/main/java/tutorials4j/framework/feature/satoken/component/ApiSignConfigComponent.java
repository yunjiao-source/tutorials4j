package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.satoken.model.ApiSignEntity;
import tutorials4j.framework.feature.satoken.service.ApiSignRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiSignConfigComponent implements CommandLineRunner {
  private final Object lock = new Object();
  private final ApiSignRepository apiSignRepository;

  public void initConfig() {
    List<ApiSignEntity> entityList = apiSignRepository.findAll();
    if (entityList.isEmpty()) {
      return;
    }

    for (ApiSignEntity apiSignEntity : entityList) {
      putToMap(apiSignEntity);
    }

    if (!SaSignManager.getSignMany().isEmpty() && log.isDebugEnabled()) {
      log.debug("初始化API Sign配置：{}", SaSignManager.getSignMany().keySet());
    }
  }

  public void addConfig(ApiSignEntity apiSignEntity) {
    validate(apiSignEntity);
    putToMap(apiSignEntity);
  }

  public void delConfig(String appName) {
    delFromMap(appName);
  }

  private void putToMap(ApiSignEntity apiSignEntity) {
    synchronized (lock) {
      SaSignConfig config = new SaSignConfig();
      config.setSecretKey(apiSignEntity.getSecretKey());
      config.setTimestampDisparity(apiSignEntity.getTimestampDisparity());
      config.setDigestAlgo(apiSignEntity.getDigestAlgo().name());

      SaSignManager.getSignMany().put(apiSignEntity.getAppName(), config);
    }
  }

  private void delFromMap(String appName) {
    synchronized (lock) {
      SaSignManager.getSignMany().remove(appName);
    }
  }

  private void validate(ApiSignEntity apiSignEntity) {
    Assert.notNull(apiSignEntity, "apiSignEntity must not be null");
    Assert.hasText(apiSignEntity.getAppName(), "apiSignEntity.appName must not be null or empty");
    Assert.hasText(
        apiSignEntity.getSecretKey(), "apiSignEntity.secretKey must not be null or empty");
    Assert.notNull(apiSignEntity.getDigestAlgo(), "apiSignEntity.digestAlgo must not be null");
  }

  @Override
  public void run(String... args) throws Exception {
    initConfig();
  }
}
