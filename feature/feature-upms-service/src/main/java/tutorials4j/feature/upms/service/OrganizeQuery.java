package tutorials4j.feature.upms.service;

import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class OrganizeQuery {
  private String parentId;
  private String name;

  private YesNoEnum enabled;

  public Specification<OrganizeEntity> buildSpecification() {
    var specList =
        List.of(
            OrganizeSpecification.parentIdEqual(parentId),
            OrganizeSpecification.nameLike(name),
            OrganizeSpecification.enabledEqual(enabled));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
