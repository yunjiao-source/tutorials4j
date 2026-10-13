package tutorials4j.feature.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Entity
@Table(
    name = "oauth_permission",
    indexes = {@Index(name = "oauth_permission_idx0", columnList = "status")},
    uniqueConstraints = {@UniqueConstraint(name = "oauth_permission_id10", columnNames = "code")})
@NamedEntityGraph(
    name = EntityGraphConsts.PERMISSION_WITH_ROLE,
    attributeNodes = @NamedAttributeNode("roles"))
public class PermissionEntity extends BaseEntity<String> {
  @Column(nullable = false)
  private String code;

  @Column private String name;

  /** 资源类型 */
  @Column private String resource;

  /** 操作类型 */
  @Column private String action;

  @Column private String description;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum status;

  @ManyToMany(mappedBy = "permissions")
  private Set<RoleEntity> roles = new HashSet<>();
}
