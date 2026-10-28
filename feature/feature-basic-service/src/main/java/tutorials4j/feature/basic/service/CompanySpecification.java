package tutorials4j.feature.basic.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.basic.entity.CompanyEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CompanySpecification {

  public static Specification<CompanyEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<CompanyEntity> shortNameLike(String shortName) {
    return (root, query, cb) ->
        StringUtils.isBlank(shortName) ? null : cb.like(root.get("shortName"), like(shortName));
  }

  public static Specification<CompanyEntity> companyTypeLike(String companyType) {
    return (root, query, cb) ->
        StringUtils.isBlank(companyType)
            ? null
            : cb.like(root.get("companyType"), like(companyType));
  }
}
