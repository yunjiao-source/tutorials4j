package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_menu",
    indexes = {@Index(name = "upms_menu_idx1", columnList = "parent_id")})
public class MenuEntity extends UpmsEntity {
  @Column(name = "parent_id", insertable = false, updatable = false)
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "upms_menu_fk1"))
  private MenuEntity parent;

  @Column private String path;

  @Column private String name;

  @Column private String component;

  @Column private String redirect;

  @Column private String title;

  @Column private String type;

  @Column private String icon;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum haveChild;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum notKeepAlive;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum hideAllChild;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum detailContent;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum ignoreAuth;

  @Column private Integer rank;

  @OneToMany(mappedBy = "parent")
  @OrderBy("path ASC")
  private List<MenuEntity> children = new ArrayList<>();

  @ManyToMany(mappedBy = "menus")
  private Set<RoleEntity> roles = new HashSet<>();

  @Override
  protected void prePersist() {
    super.prePersist();

    if (haveChild == null) {
      haveChild = YesNoEnum.no;
    }

    if (notKeepAlive == null) {
      notKeepAlive = YesNoEnum.no;
    }

    if (hideAllChild == null) {
      hideAllChild = YesNoEnum.no;
    }

    if (detailContent == null) {
      detailContent = YesNoEnum.no;
    }

    if (ignoreAuth == null) {
      ignoreAuth = YesNoEnum.no;
    }
  }
}
