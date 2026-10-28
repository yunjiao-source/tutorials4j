package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.MenuSpecification.allRoot;
import static tutorials4j.feature.upms.service.MenuSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.nameLike;
import static tutorials4j.feature.upms.service.MenuSpecification.parentIdEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.pathLike;
import static tutorials4j.feature.upms.service.MenuSpecification.titleLike;
import static tutorials4j.feature.upms.service.MenuSpecification.typeIn;
import static tutorials4j.feature.upms.service.MenuSpecification.typeNotIn;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.feature.upms.entity.MenuType;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuQuery extends BaseQuery<MenuEntity> {
  private YesNoEnum allRoot;
  private String parentId;
  private String name;
  private String path;
  private String title;
  private YesNoEnum enabled;
  private Set<MenuType> typesIn;
  private Set<MenuType> typesNotIn;

  @Override
  protected void addSpec() {
    addSpec(parentIdEqual(parentId))
        .addSpec(YesNoEnum.isTrue(allRoot), allRoot())
        .addSpec(nameLike(name))
        .addSpec(pathLike(path))
        .addSpec(titleLike(title))
        .addSpec(enabledEqual(enabled))
        .addSpec(typeIn(typesIn))
        .addSpec(typeNotIn(typesNotIn));
  }
}
