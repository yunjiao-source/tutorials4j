package tutorials4j.framework.feature.satoken.model;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;
import tutorials4j.framework.oauth.satoken.common.SaTokenOauthConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ApiKeyUpdateModel {
  @NotBlank(message = "命名空间是必须的")
  private String namespace = SaTokenOauthConsts.DEFAULT_NAMESPACE;

  @NotNull(message = "loginId是必须的", groups = ValidGroup.AdminGroup.class)
  private Object loginId;

  @NotBlank(message = "apiKey是必须的")
  private String apiKey;

  @NotNull(message = "有效标记是必须的")
  private Boolean isValid;

  @NotNull(message = "过期时间是必须的")
  @Min(value = 1, message = "过期时间必须大于0")
  private long expiresTime;

  @NotBlank(message = "标题是必须的")
  private String title;

  private String intro;

  private List<String> scopes;

  private Map<String, String> extraData;

  public void fillTo(ApiKeyModel apiKeyModel) {
    Assert.notNull(apiKeyModel, "apiKeyModel must not be null");

    apiKeyModel.setExpiresTime(expiresTime);
    apiKeyModel.setIsValid(isValid);
    apiKeyModel.setTitle(title);
    apiKeyModel.setIntro(intro);
    if (!CollectionUtils.sizeIsEmpty(scopes)) {
      apiKeyModel.setScopes(scopes);
    }
    if (!CollectionUtils.sizeIsEmpty(extraData)) {
      // 设置扩展信息
      Map<String, Object> tmpMap = new HashMap<>(extraData.size());
      tmpMap.putAll(extraData);
      apiKeyModel.setExtraData(tmpMap);
    }
  }
}
