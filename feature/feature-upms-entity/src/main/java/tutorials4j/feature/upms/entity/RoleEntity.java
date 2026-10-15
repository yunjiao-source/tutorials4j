package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
    name = "upms_role",
    indexes = {@Index(name = "upms_role_idx0", columnList = "status")},
    uniqueConstraints = {@UniqueConstraint(name = "upms_role_id10", columnNames = "code")})
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = EntityGraphConsts.ROLE_WITH_USER,
      attributeNodes = @NamedAttributeNode("users")),
  @NamedEntityGraph(
      name = EntityGraphConsts.ROLE_WITH_PERMISSION,
      attributeNodes = @NamedAttributeNode("permissions"))
})
public class RoleEntity extends BaseEntity<String> {
  @Column(nullable = false)
  private String code;

  @Column private String name;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum status;

  @Column private String description;

  @ManyToMany(mappedBy = "roles")
  private Set<UserEntity> users = new HashSet<>();

  @ManyToMany
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "upms_role_permission",
      joinColumns = {@JoinColumn(name = "role_id")},
      inverseJoinColumns = {@JoinColumn(name = "permission_id")},
      uniqueConstraints = {@UniqueConstraint(columnNames = {"role_id", "permission_id"})},
      indexes = {
        @Index(name = "upms_role_permission_idx1", columnList = "role_id"),
        @Index(name = "upms_role_permission_idx2", columnList = "permission_id")
      })
  private Set<PermissionEntity> permissions = new HashSet<>();
}
