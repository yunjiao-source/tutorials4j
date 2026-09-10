package tutorials4j.framework.feature.satoken.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;
import tutorials4j.framework.oauth.satoken.common.DigestAlgo;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ApiSignModel {
  @NotBlank(message = "应用名称是必须的")
  private String appName;

  @NotNull(message = "摘要算法是必须的")
  private DigestAlgo digestAlgo;

  private long timestampDisparity = -1L;

  public void fillTo(ApiSignEntity apiSignEntity) {
    Assert.notNull(apiSignEntity, "apiSignEntity must not be null");

    apiSignEntity.setAppName(appName);
    apiSignEntity.setTimestampDisparity(timestampDisparity);
    apiSignEntity.setDigestAlgo(digestAlgo);
  }
}
