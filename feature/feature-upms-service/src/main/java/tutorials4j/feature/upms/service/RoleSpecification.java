package tutorials4j.feature.upms.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.RoleEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class RoleSpecification {

  public static Specification<RoleEntity> codeLike(String code) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(code)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("code"), code);
    };
  }

  public static Specification<RoleEntity> nameLike(String name) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("name"), name);
    };
  }
}
