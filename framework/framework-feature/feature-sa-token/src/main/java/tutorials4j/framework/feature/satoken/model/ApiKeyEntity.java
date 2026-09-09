package tutorials4j.framework.feature.satoken.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.core.support.HashMapConverter;
import tutorials4j.framework.data.core.support.ListConverter;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(
    name = "feat_api_key",
    indexes = {@Index(name = "feat_api_key_idx0", columnList = "namespace, loginId")},
    uniqueConstraints = {@UniqueConstraint(name = "feat_api_key_idx1", columnNames = "api_key")})
@EqualsAndHashCode(callSuper = false)
public class ApiKeyEntity extends BaseEntity {

  @Column private String namespace;

  @Column private String title;

  @Column private String intro;

  @Column(nullable = false)
  private String apiKey;

  @Column(nullable = false)
  private String loginId;

  @Column private Long expiresTime;

  @Column private Boolean isValid;

  @Column
  @Convert(converter = ListConverter.class)
  private List<String> scopes = new ArrayList<>();

  @Column
  @Convert(converter = HashMapConverter.class)
  private Map<String, String> extraData = new HashMap<>();
}
