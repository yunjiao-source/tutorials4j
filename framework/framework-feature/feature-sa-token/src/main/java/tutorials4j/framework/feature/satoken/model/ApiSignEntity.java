package tutorials4j.framework.feature.satoken.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;
import tutorials4j.framework.oauth.satoken.common.DigestAlgo;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(
    name = "feat_api_sign",
    indexes = {@Index(name = "feat_api_sign_idx0", columnList = "appName")})
@EqualsAndHashCode(callSuper = false)
public class ApiSignEntity extends BaseEntity {

  @Column(nullable = false)
  private String appName;

  @Column(nullable = false)
  private String secretKey;

  @Column private long timestampDisparity;

  @Column private DigestAlgo digestAlgo;
}
