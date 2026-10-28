package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.service.ConfigSpecification.categoryEqual;
import static tutorials4j.feature.basic.service.ConfigSpecification.descriptionLike;
import static tutorials4j.feature.basic.service.ConfigSpecification.enabledEqual;
import static tutorials4j.feature.basic.service.ConfigSpecification.keyLike;
import static tutorials4j.feature.basic.service.ConfigSpecification.nameLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.basic.entity.ConfigEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigQuery extends BaseQuery<ConfigEntity> {
  private String name;
  private String key;
  private String category;
  private String description;
  private YesNoEnum enabled;

  @Override
  protected void addSpec() {
    addSpec(nameLike(name))
        .addSpec(keyLike(key))
        .addSpec(categoryEqual(category))
        .addSpec(descriptionLike(description))
        .addSpec(enabledEqual(enabled));
  }
}
