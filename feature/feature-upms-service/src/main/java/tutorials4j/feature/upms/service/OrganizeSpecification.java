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
    return (root, query, cb) ->
        StringUtils.isBlank(parentId) ? null : cb.equal(root.get("parentId"), parentId);
  }

  public static Specification<OrganizeEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<OrganizeEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<OrganizeEntity> enabledIn(Collection<YesNoEnum> allEnabled) {
    return (root, query, cb) ->
        allEnabled == null ? null : cb.in(root.get("enabled").in(allEnabled));
  }
}
