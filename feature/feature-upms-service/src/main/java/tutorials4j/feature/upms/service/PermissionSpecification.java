package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.PermissionEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class PermissionSpecification {
  public static Specification<PermissionEntity> nameLike(String name) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) {
        return cb.conjunction();
      }
      return cb.like(root.get("name"), like(name));
    };
  }

  public static Specification<PermissionEntity> codeLike(String code) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(code)) {
        return cb.conjunction();
      }
      return cb.like(root.get("code"), like(code));
    };
  }
}
