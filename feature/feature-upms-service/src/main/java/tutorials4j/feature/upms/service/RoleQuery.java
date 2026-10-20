package tutorials4j.feature.upms.service;

import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.RoleEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class RoleQuery {
  private String code;
  private String name;

  public Specification<RoleEntity> buildSpecification() {
    var specList = List.of(RoleSpecification.codeLike(code), RoleSpecification.nameLike(name));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
