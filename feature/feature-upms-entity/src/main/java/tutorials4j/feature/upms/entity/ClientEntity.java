package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.convert.ListAttributeConverter;
import tutorials4j.toolkit.data.exception.DataErrorCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_client",
    indexes = {@Index(name = "upms_client_idx1", columnList = "subject_id")},
    uniqueConstraints = {@UniqueConstraint(name = "upms_client_uni1", columnNames = "client_id")})
public class ClientEntity extends UpmsEntity {
  /** 应用id，应该全局唯一 */
  @Column(nullable = false)
  private String clientId;

  /** 应用秘钥 */
  @Column private String clientSecret;

  @Column(nullable = false)
  private String clientName;

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

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isReserved;

  @Override
  protected void prePersist() {
    super.prePersist();

    if (isReserved == null) {
      isReserved = YesNoEnum.no;
    }
  }

  @Override
  protected void preRemove() {
    super.preRemove();

    if (Objects.equals(isReserved, YesNoEnum.yes)) {
      throw DataErrorCode.RESERVED_DATA_CAN_NOT_REMOVE.throwed().param("clientName", clientName);
    }
  }
}
