package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.feature.upms.entity.MenuType;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class MenuSpecification {

  public static Specification<MenuEntity> allRoot() {
    return (root, query, cb) -> cb.isNull(root.get("parent"));
  }

  public static Specification<MenuEntity> parentIdEqual(String parentId) {
    return (root, query, cb) ->
        StringUtils.isBlank(parentId) ? null : cb.equal(root.get("parentId"), parentId);
  }

  public static Specification<MenuEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<MenuEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<MenuEntity> pathLike(String path) {
    return (root, query, cb) ->
        StringUtils.isBlank(path) ? null : cb.like(root.get("path"), like(path));
  }

  public static Specification<MenuEntity> titleLike(String title) {
    return (root, query, cb) ->
        StringUtils.isBlank(title) ? null : cb.like(root.get("title"), like(title));
  }

  public static Specification<MenuEntity> typeIn(Collection<MenuType> menuTypes) {
    return (root, query, cb) -> menuTypes == null ? null : root.get("type").in(menuTypes);
  }

  public static Specification<MenuEntity> typeNotIn(Collection<MenuType> menuTypes) {
    return Specification.not(typeIn(menuTypes));
  }
}
