package tutorials4j.feature.upms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    indexes = {@Index(name = "upms_organize_idx0", columnList = "parent_id")})
public class OrganizeEntity extends UpmsEntity {
  @ManyToOne
  @JoinColumn(name = "parent_id")
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

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "company_id", unique = true)
  private CompanyEntity company;
}
