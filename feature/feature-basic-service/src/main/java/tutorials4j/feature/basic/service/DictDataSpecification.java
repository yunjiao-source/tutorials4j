package tutorials4j.feature.basic.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.basic.entity.DictDataEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DictDataSpecification {
  public static Specification<DictDataEntity> allRoot() {
    return (root, query, cb) -> cb.isNull(root.get("parent"));
  }

  public static Specification<DictDataEntity> parentIdEqual(String parentId) {
    return (root, query, cb) ->
        StringUtils.isBlank(parentId) ? null : cb.equal(root.get("parentId"), parentId);
  }

  public static Specification<DictDataEntity> dictTypeIdEqual(String dictTypeId) {
    return (root, query, cb) ->
        StringUtils.isBlank(dictTypeId) ? null : cb.equal(root.get("dictTypeId"), dictTypeId);
  }

  public static Specification<DictDataEntity> enabledEqual(Boolean enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<DictDataEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<DictDataEntity> codeLike(String code) {
    return (root, query, cb) ->
        StringUtils.isBlank(code) ? null : cb.like(root.get("code"), like(code));
  }

  public static Specification<DictDataEntity> descriptionLike(String description) {
    return (root, query, cb) ->
        StringUtils.isBlank(description)
            ? null
            : cb.like(root.get("description"), like(description));
  }
}
