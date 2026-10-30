package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class PermissionSpecification {
  public static Specification<PermissionEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<PermissionEntity> codeLike(String code) {
    return (root, query, cb) ->
        StringUtils.isBlank(code) ? null : cb.like(root.get("code"), like(code));
  }

  public static Specification<PermissionEntity> descriptionLike(String description) {
    return (root, query, cb) ->
        StringUtils.isBlank(description)
            ? null
            : cb.like(root.get("description"), like(description));
  }

  public static Specification<PermissionEntity> codeEqual(String code) {
    return (root, query, cb) -> StringUtils.isBlank(code) ? null : cb.equal(root.get("code"), code);
  }

  public static Specification<PermissionEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<PermissionEntity> reservedEqual(YesNoEnum reserved) {
    return (root, query, cb) -> reserved == null ? null : cb.equal(root.get("reserved"), reserved);
  }
}
