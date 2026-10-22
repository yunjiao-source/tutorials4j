package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class ClientSpecification {
  public static Specification<ClientEntity> clientIdLike(String clientId) {
    return (root, query, cb) ->
        StringUtils.isBlank(clientId) ? null : cb.like(root.get("clientId"), like(clientId));
  }

  public static Specification<ClientEntity> subjectIdLike(String subjectId) {
    return (root, query, cb) ->
        StringUtils.isBlank(subjectId) ? null : cb.like(root.get("subjectId"), like(subjectId));
  }

  public static Specification<ClientEntity> isNewRefreshEqual(YesNoEnum isNewRefresh) {
    return (root, query, cb) ->
        isNewRefresh == null ? null : cb.equal(root.get("isNewRefresh"), isNewRefresh);
  }

  public static Specification<ClientEntity> isAutoConfirmEqual(YesNoEnum isAutoConfirm) {
    return (root, query, cb) ->
        isAutoConfirm == null ? null : cb.equal(root.get("isAutoConfirm"), isAutoConfirm);
  }
}
