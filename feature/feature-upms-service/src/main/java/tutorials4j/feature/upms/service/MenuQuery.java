package tutorials4j.feature.upms.service;

import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.MenuEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class MenuQuery {
  private String parentId;
  private String name;
  private String path;
  private String title;

  public Specification<MenuEntity> buildSpecification() {
    var specList =
        List.of(
            MenuSpecification.parentIdEqual(parentId),
            MenuSpecification.nameLike(name),
            MenuSpecification.pathLike(path),
            MenuSpecification.titleLike(title));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
