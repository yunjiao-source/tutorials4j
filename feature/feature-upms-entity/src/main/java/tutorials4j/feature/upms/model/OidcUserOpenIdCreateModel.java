package tutorials4j.feature.upms.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@Builder
public class OidcUserOpenIdCreateModel {
  private String userId;

  private String clientId;

  private String prefix;

  private String openId;

  private Map<String, String> attrMap;

  public void validate() {
    if (StringUtils.isAnyBlank(userId, clientId, prefix, openId)) {
      throw UpmsFeatureErrorCode.OPEN_ID_CREATE_FAIL
          .throwed()
          .param("userId", userId)
          .param("clientId", clientId)
          .param("prefix", prefix)
          .param("openId", openId);
    }
  }
}
