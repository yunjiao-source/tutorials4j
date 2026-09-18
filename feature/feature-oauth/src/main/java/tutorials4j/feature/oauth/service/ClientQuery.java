package tutorials4j.feature.oauth.service;

import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ClientQuery {
  private String clientId;
  private String subjectId;
  private YesNoEnum isNewRefresh;
  private YesNoEnum isAutoConfirm;

  public Specification<ClientEntity> buildSpecification() {
    var specList =
        List.of(
            ClientSpecification.clientIdLike(clientId),
            ClientSpecification.subjectIdLike(subjectId),
            ClientSpecification.isNewRefreshEqual(isNewRefresh),
            ClientSpecification.isAutoConfirmEqual(isAutoConfirm));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
