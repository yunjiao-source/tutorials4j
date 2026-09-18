package tutorials4j.feature.oauth.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ClientSpecification {
  public static Specification<ClientEntity> clientIdLike(String clientId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(clientId)) {
        return cb.conjunction();
      }
      return cb.like(root.get("clientId"), like(clientId));
    };
  }

  public static Specification<ClientEntity> subjectIdLike(String subjectId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(subjectId)) {
        return cb.conjunction();
      }
      return cb.like(root.get("subjectId"), like(subjectId));
    };
  }

  public static Specification<ClientEntity> isNewRefreshEqual(YesNoEnum isNewRefresh) {
    return (root, query, cb) -> {
      if (isNewRefresh == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("isNewRefresh"), isNewRefresh);
    };
  }

  public static Specification<ClientEntity> isAutoConfirmEqual(YesNoEnum isAutoConfirm) {
    return (root, query, cb) -> {
      if (isAutoConfirm == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("isAutoConfirm"), isAutoConfirm);
    };
  }
}
