package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class RoleSpecification {

  public static Specification<RoleEntity> codeLike(String code) {
    return (root, query, cb) ->
        StringUtils.isBlank(code) ? null : cb.like(root.get("code"), like(code));
  }

  public static Specification<RoleEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<RoleEntity> descriptionLike(String description) {
    return (root, query, cb) ->
        StringUtils.isBlank(description)
            ? null
            : cb.like(root.get("description"), like(description));
  }

  public static Specification<RoleEntity> codeEqual(String code) {
    return (root, query, cb) -> StringUtils.isBlank(code) ? null : cb.equal(root.get("code"), code);
  }

  public static Specification<RoleEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<RoleEntity> reservedEqual(YesNoEnum reserved) {
    return (root, query, cb) -> reserved == null ? null : cb.equal(root.get("reserved"), reserved);
  }

  public static Specification<RoleEntity> createdDateGte(Instant createdDateBegin) {
    return (root, query, cb) ->
        createdDateBegin == null
            ? null
            : cb.greaterThanOrEqualTo(root.get("createdDate"), createdDateBegin);
  }

  public static Specification<RoleEntity> createdDateLte(Instant createdDateEnd) {
    return (root, query, cb) ->
        createdDateEnd == null
            ? null
            : cb.lessThanOrEqualTo(root.get("createdDate"), createdDateEnd);
  }
}
