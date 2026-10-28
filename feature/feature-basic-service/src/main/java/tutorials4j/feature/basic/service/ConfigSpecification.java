package tutorials4j.feature.basic.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.basic.entity.ConfigEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ConfigSpecification {

  public static Specification<ConfigEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<ConfigEntity> keyLike(String key) {
    return (root, query, cb) ->
        StringUtils.isBlank(key) ? null : cb.like(root.get("key"), like(key));
  }

  public static Specification<ConfigEntity> descriptionLike(String description) {
    return (root, query, cb) ->
        StringUtils.isBlank(description)
            ? null
            : cb.like(root.get("description"), like(description));
  }

  public static Specification<ConfigEntity> categoryEqual(String category) {
    return (root, query, cb) ->
        StringUtils.isBlank(category) ? null : cb.like(root.get("category"), like(category));
  }

  public static Specification<ConfigEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }
}
