package tutorials4j.feature.basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
    name = "basic_config",
    indexes = {
      @Index(name = "basic_config_idx1", columnList = "code"),
      @Index(name = "basic_config_idx2", columnList = "category")
    })
public class ConfigEntity extends BasicEntity {
  @Column private String name;

  @Column private String code;

  @Column private String value;

  @Column private String category;

  @Column private String description;
}
