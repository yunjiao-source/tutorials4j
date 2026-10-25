package tutorials4j.feature.basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
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
    name = "basic_dict_type",
    indexes = {@Index(name = "basic_dict_type_idx1", columnList = "parent_id")},
    uniqueConstraints = {@UniqueConstraint(name = "basic_dict_type_uni1", columnNames = "code")})
public class DictTypeEntity extends BasicEntity {
  @Column(name = "parent_id", insertable = false, updatable = false)
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "basic_dict_type_fk1"))
  private DictTypeEntity parent;

  @Column private String name;

  @Column private String code;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isTree;

  @Column private String description;

  @Column private Integer ordered;

  @OneToMany(mappedBy = "parent")
  @OrderBy("ordered ASC")
  private List<DictTypeEntity> children = new ArrayList<>();

  @OneToMany(mappedBy = "dictType")
  @OrderBy("ordered ASC")
  private List<DictDataEntity> dictDataList = new ArrayList<>();
}
