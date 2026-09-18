package tutorials4j.feature.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.toolkit.core.enums.YesNoEnum;
import tutorials4j.toolkit.data.convert.ListAttributeConverter;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(
    name = "oauth_client",
    indexes = {@Index(name = "oauth_client_idx0", columnList = "subject_id")},
    uniqueConstraints = {@UniqueConstraint(name = "oauth_client_idx1", columnNames = "client_id")})
@EqualsAndHashCode(callSuper = false)
public class ClientEntity extends BaseEntity<String> {
  /** 应用id，应该全局唯一 */
  @Column(nullable = false)
  private String clientId;

  /** 应用秘钥 */
  @Column private String clientSecret;

  /** 应用签约的所有权限 */
  @Column(length = 2000)
  @Convert(converter = ListAttributeConverter.class)
  private List<String> contractScopes;

  /** 应用允许授权的所有URL（可以使用 * 号通配符） */
  @Column(length = 2000)
  @Convert(converter = ListAttributeConverter.class)
  private List<String> allowRedirectUris;

  /** 应用允许的所有 grant_type */
  @Column(length = 2000)
  @Convert(converter = ListAttributeConverter.class)
  private List<String> allowGrantTypes;

  /** 应用主体id */
  @Column private String subjectId;

  /** 此应用Access-Token 保存的时间（单位：秒） */
  @Column private Long accessTokenTimeout;

  /** 此应用Refresh-Token 保存的时间（单位：秒） */
  @Column private Long refreshTokenTimeout;

  /** 此应用Client-Token 保存的时间（单位：秒） */
  @Column private Long clientTokenTimeout;

  /** 此应用单个用户最多同时存在的 Access-Token 数量 */
  @Column private Integer maxAccessTokenCount;

  /** 此应用单个用户最多同时存在的 Refresh-Token 数量 */
  @Column private Integer maxRefreshTokenCount;

  /** 此应用最多同时存在的 Client-Token 数量 */
  @Column private Integer maxClientTokenCount;

  /** 是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isNewRefresh;

  /** 是否允许此应用自动确认授权 （高危配置，禁止向不被信任的第三方开启此选项） */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isAutoConfirm;
}
