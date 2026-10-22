package tutorials4j.feature.basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
      @Index(name = "basic_config_idx1", columnList = "key"),
      @Index(name = "basic_config_idx2", columnList = "category")
    },
    uniqueConstraints = {@UniqueConstraint(name = "basic_config_uni1", columnNames = "code")})
public class ConfigEntity extends BasicEntity {
  @Column private String name;

  @Column private String key;

  @Column private String value;

  @Column private String category;

  @Column private String description;
}
