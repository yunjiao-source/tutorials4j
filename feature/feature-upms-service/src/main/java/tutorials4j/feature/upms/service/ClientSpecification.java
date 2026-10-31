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
  public static Specification<ClientEntity> clientNameLike(String clientName) {
    return (root, query, cb) ->
        StringUtils.isBlank(clientName) ? null : cb.like(root.get("clientName"), like(clientName));
  }

  public static Specification<ClientEntity> clientIdEqual(String clientId) {
    return (root, query, cb) ->
        StringUtils.isBlank(clientId) ? null : cb.equal(root.get("clientId"), clientId);
  }

  public static Specification<ClientEntity> subjectIdEqual(String subjectId) {
    return (root, query, cb) ->
        StringUtils.isBlank(subjectId) ? null : cb.equal(root.get("subjectId"), subjectId);
  }

  public static Specification<ClientEntity> isNewRefreshEqual(YesNoEnum isNewRefresh) {
    return (root, query, cb) ->
        isNewRefresh == null ? null : cb.equal(root.get("isNewRefresh"), isNewRefresh);
  }

  public static Specification<ClientEntity> isAutoConfirmEqual(YesNoEnum isAutoConfirm) {
    return (root, query, cb) ->
        isAutoConfirm == null ? null : cb.equal(root.get("isAutoConfirm"), isAutoConfirm);
  }

  public static Specification<ClientEntity> enabledEqual(YesNoEnum enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<ClientEntity> reservedEqual(YesNoEnum reserved) {
    return (root, query, cb) -> reserved == null ? null : cb.equal(root.get("reserved"), reserved);
  }
}
