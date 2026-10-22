package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_permission",
    indexes = {@Index(name = "upms_permission_idx1", columnList = "code,enabled")},
    uniqueConstraints = {@UniqueConstraint(name = "upms_permission_uni0", columnNames = "code")})
public class PermissionEntity extends UpmsEntity {
  @Column(nullable = false)
  private String code;

  @Column private String name;

  /** 资源类型 */
  @Column private String resource;

  /** 操作类型 */
  @Column private String action;

  @Column private String description;

  @ManyToMany(mappedBy = "permissions")
  private Set<RoleEntity> roles = new HashSet<>();
}
