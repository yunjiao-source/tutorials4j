package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;
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
    name = "upms_role",
    indexes = {@Index(name = "upms_role_idx1", columnList = "code,enabled")},
    uniqueConstraints = {@UniqueConstraint(name = "upms_role_uni1", columnNames = "code")})
public class RoleEntity extends UpmsEntity {
  @Column(nullable = false)
  private String code;

  @Column private String name;

  @Column private String description;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isReserved;

  @ManyToMany(mappedBy = "roles")
  private Set<UserEntity> users = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "upms_role_permission",
      joinColumns = {
        @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "upms_role_permission_fk1"))
      },
      inverseJoinColumns = {
        @JoinColumn(
            name = "permission_id",
            foreignKey = @ForeignKey(name = "upms_role_permission_fk2"))
      },
      uniqueConstraints = {
        @UniqueConstraint(
            name = "upms_role_permission_uni1",
            columnNames = {"role_id", "permission_id"})
      },
      indexes = {
        @Index(name = "upms_role_permission_idx1", columnList = "role_id"),
        @Index(name = "upms_role_permission_idx2", columnList = "permission_id")
      })
  private Set<PermissionEntity> permissions = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "upms_role_menu",
      joinColumns = {
        @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "upms_role_menu_fk1"))
      },
      inverseJoinColumns = {
        @JoinColumn(name = "menu_id", foreignKey = @ForeignKey(name = "upms_role_menu_fk2"))
      },
      uniqueConstraints = {
        @UniqueConstraint(
            name = "upms_role_menu_uni1",
            columnNames = {"role_id", "menu_id"})
      },
      indexes = {
        @Index(name = "upms_role_menu_idx1", columnList = "menu_id"),
        @Index(name = "upms_role_menu_idx2", columnList = "role_id")
      })
  private Set<MenuEntity> menus = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "upms_role_organize",
      joinColumns = {
        @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "upms_role_organize_fk1"))
      },
      inverseJoinColumns = {
        @JoinColumn(name = "organize_id", foreignKey = @ForeignKey(name = "upms_role_organize_fk2"))
      },
      uniqueConstraints = {
        @UniqueConstraint(
            name = "upms_role_organize_uni1",
            columnNames = {"role_id", "organize_id"})
      },
      indexes = {
        @Index(name = "upms_role_organize_idx1", columnList = "organize_id"),
        @Index(name = "upms_role_organize_idx2", columnList = "role_id")
      })
  private List<OrganizeEntity> organizes = new ArrayList<>();

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
      throw DataErrorCode.RESERVED_DATA_CAN_NOT_REMOVE.throwed().param("name", name);
    }
  }
}
