package tutorials4j.feature.upms.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class AuthHistoryQuery {
  private String userId;
  private AuthStatus authStatus;
  private Instant createdTimeBegin;
  private Instant createdTimeEnd;

  public Specification<AuthHistoryEntity> buildSpecification() {
    var specList =
        List.of(
            AuthHistorySpecification.userIdEqual(userId),
            AuthHistorySpecification.authStatusEqual(authStatus),
            AuthHistorySpecification.createdTimeGte(createdTimeBegin),
            AuthHistorySpecification.createdTimeLte(createdTimeEnd));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
