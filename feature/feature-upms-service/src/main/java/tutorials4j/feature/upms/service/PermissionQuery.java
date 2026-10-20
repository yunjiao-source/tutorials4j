package tutorials4j.feature.upms.service;

import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.PermissionEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class PermissionQuery {
  private String code;

  private String name;

  public Specification<PermissionEntity> buildSpecification() {
    var specList =
        List.of(PermissionSpecification.codeLike(code), PermissionSpecification.nameLike(name));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
