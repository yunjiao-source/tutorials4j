package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class OrganizeSpecification {
  public static Specification<OrganizeEntity> allRoot() {
    return (root, query, cb) -> cb.isNull(root.get("parent"));
  }

  public static Specification<OrganizeEntity> parentIdEqual(String parentId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(parentId)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("parent").get("id"), parentId);
    };
  }

  public static Specification<OrganizeEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> {
      if (enabled == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("enabled"), enabled);
    };
  }

  public static Specification<OrganizeEntity> nameLike(String name) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) {
        return cb.conjunction();
      }
      return cb.like(root.get("name"), like(name));
    };
  }

  public static Specification<OrganizeEntity> enabledIn(Collection<YesNoEnum> allEnabled) {
    return (root, query, cb) -> {
      if (allEnabled == null) {
        return cb.conjunction();
      }
      return cb.in(root.get("enabled").in(allEnabled));
    };
  }
}
