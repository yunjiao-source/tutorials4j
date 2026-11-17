package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.MenuSpecification.allRoot;
import static tutorials4j.feature.upms.service.MenuSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.idEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.nameEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.nameLike;
import static tutorials4j.feature.upms.service.MenuSpecification.parentIdEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.pathEqual;
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
  private String id;
  private YesNoEnum allRoot;
  private String parentId;
  private String name;
  private String path;
  private String nameLike;
  private String pathLike;
  private String titleLike;
  private YesNoEnum enabled;
  private Set<MenuType> typesIn;
  private Set<MenuType> typesNotIn;

  @Override
  protected void addSpec() {
    addSpec(idEqual(id))
        .addSpec(parentIdEqual(parentId))
        .addSpec(YesNoEnum.isTrue(allRoot), allRoot())
        .addSpec(nameEqual(name))
        .addSpec(pathEqual(path))
        .addSpec(nameLike(nameLike))
        .addSpec(pathLike(pathLike))
        .addSpec(titleLike(titleLike))
        .addSpec(enabledEqual(enabled))
        .addSpec(typeIn(typesIn))
        .addSpec(typeNotIn(typesNotIn));
  }
}
