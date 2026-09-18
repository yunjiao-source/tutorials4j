package tutorials4j.feature.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.toolkit.data.convert.MapAttributeConverter;
import tutorials4j.toolkit.data.hibernate.domain.BaseIdEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(
    name = "oauth_open_user",
    uniqueConstraints = {
      @UniqueConstraint(name = "oauth_open_user_idx0", columnNames = "user_id,client_id"),
      @UniqueConstraint(name = "oauth_open_user_idx1", columnNames = "user_id,subject_id")
    })
@EqualsAndHashCode(callSuper = false)
public class OpenUserEntity extends BaseIdEntity<String> {
  @Column(nullable = false)
  private String userId;

  @Column private String clientId;
  @Column private String subjectId;

  @Column(nullable = false)
  private String prefix;

  @Column private String openId;

  @Column private String unionId;

  @Column private Instant createTime;

  @Column
  @Convert(converter = MapAttributeConverter.class)
  private Map<String, String> attrMap = new HashMap<>();
}
