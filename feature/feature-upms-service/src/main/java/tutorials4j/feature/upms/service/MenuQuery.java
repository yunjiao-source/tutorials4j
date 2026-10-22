package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.MenuSpecification.nameLike;
import static tutorials4j.feature.upms.service.MenuSpecification.parentIdEqual;
import static tutorials4j.feature.upms.service.MenuSpecification.pathLike;
import static tutorials4j.feature.upms.service.MenuSpecification.titleLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.MenuEntity;
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
  private String parentId;
  private String name;
  private String path;
  private String title;

  @Override
  protected void addSpec() {
    addSpec(parentIdEqual(parentId))
        .addSpec(nameLike(name))
        .addSpec(pathLike(path))
        .addSpec(titleLike(title));
  }
}
