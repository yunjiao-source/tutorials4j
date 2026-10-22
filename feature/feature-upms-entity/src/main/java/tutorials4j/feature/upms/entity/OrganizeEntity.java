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

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_organize",
    indexes = {@Index(name = "upms_organize_idx1", columnList = "parent_id")})
public class OrganizeEntity extends UpmsEntity {
  @Column(name = "parent_id", insertable = false, updatable = false)
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "upms_organize_fk1"))
  private OrganizeEntity parent;

  @Column private String name;

  @Column private String shortName;

  @Column private String code;

  @Column private Integer rank;

  @Column
  @Enumerated(EnumType.STRING)
  private OrganizeCategory category;

  @OneToMany(mappedBy = "parent")
  @OrderBy("rank ASC")
  private List<OrganizeEntity> children = new ArrayList<>();

  @ManyToMany(mappedBy = "organizes")
  private Set<RoleEntity> roles = new HashSet<>();
}
