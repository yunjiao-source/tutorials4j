package tutorials4j.feature.oauth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class ClientUpdateModel {

  @NotBlank(message = "名称是必须的")
  private String clientName;

  private String clientSecret;

  @NotEmpty(message = "应用签约的所有权限是必须的")
  private List<String> contractScopes;

  @NotEmpty(message = "应用允许授权的所有URL是必须的")
  private List<String> allowRedirectUris;

  @NotEmpty(message = "应用允许的所有类型（grant_type）是必须的")
  private List<String> allowGrantTypes;

  private String subjectId;

  private Long accessTokenTimeout;

  private Long refreshTokenTimeout;

  private Long clientTokenTimeout;

  private Integer maxAccessTokenCount;

  private Integer maxRefreshTokenCount;

  private Integer maxClientTokenCount;

  private YesNoEnum isNewRefresh;

  private YesNoEnum isAutoConfirm;
}
