package tutorials4j.feature.oauth.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.oauth.entity.AuthHistoryEntity;
import tutorials4j.feature.oauth.entity.AuthStatus;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class AuthHistoryQuery {
  private String userId;
  private AuthStatus authStatus;
  private Instant createTimeBegin;
  private Instant createTimeEnd;

  public Specification<AuthHistoryEntity> buildSpecification() {
    var specList =
        List.of(
            AuthHistorySpecification.userIdEqual(userId),
            AuthHistorySpecification.authStatusEqual(authStatus),
            AuthHistorySpecification.createdTimeGte(createTimeBegin),
            AuthHistorySpecification.createdTimeLte(createTimeEnd));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
