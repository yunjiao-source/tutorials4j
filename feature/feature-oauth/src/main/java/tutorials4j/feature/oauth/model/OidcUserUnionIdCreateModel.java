package tutorials4j.feature.oauth.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.feature.oauth.exception.OAuthFeatureErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class OidcUserUnionIdCreateModel {
  private String userId;
  private String subjectId;

  private String prefix;

  private String unionId;

  private Map<String, String> attrMap;

  public void validate() {
    if (StringUtils.isAnyBlank(userId, subjectId, prefix, unionId)) {
      throw OAuthFeatureErrorCode.UNION_ID_CREATE_FAIL
          .throwed()
          .param("userId", userId)
          .param("subjectId", subjectId)
          .param("prefix", prefix)
          .param("unionId", unionId);
    }
  }
}
