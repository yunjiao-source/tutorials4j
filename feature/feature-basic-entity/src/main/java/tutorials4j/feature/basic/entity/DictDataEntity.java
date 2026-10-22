package tutorials4j.feature.basic.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.data.convert.MapAttributeConverter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "basic_dict_data",
    indexes = {
      @Index(name = "basic_dict_data_idx1", columnList = "code"),
      @Index(name = "basic_dict_data_idx2", columnList = "parent_id"),
      @Index(name = "basic_dict_data_idx3", columnList = "dict_type_id")
    })
public class DictDataEntity extends BasicEntity {
  @Column(name = "parent_id", insertable = false, updatable = false)
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "basic_dict_data_fk1"))
  private DictDataEntity parent;

  @Column private String name;

  @Column private String code;

  @Column private String description;

  @Column private Integer rank;

  @Column(name = "dict_type_id", insertable = false, updatable = false)
  private String dictTypeId;

  @Column
  @Convert(converter = MapAttributeConverter.class)
  private Map<String, String> jsonData = new HashMap<>();

  @OneToMany(mappedBy = "parent")
  @OrderBy("rank ASC")
  private List<DictDataEntity> children = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dict_type_id", foreignKey = @ForeignKey(name = "basic_dict_data_fk2"))
  private DictTypeEntity dictType;
}
