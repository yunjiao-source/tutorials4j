package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.AuthHistorySpecification.authStatusEqual;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.createdTimeGte;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.createdTimeLte;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.userIdEqual;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthHistoryQuery extends BaseQuery<AuthHistoryEntity> {
  private String userId;
  private AuthStatus authStatus;
  private Instant createdTimeBegin;
  private Instant createdTimeEnd;

  @Override
  protected void addSpec() {
    addSpec(userIdEqual(userId))
        .addSpec(authStatusEqual(authStatus))
        .addSpec(createdTimeGte(createdTimeBegin))
        .addSpec(createdTimeLte(createdTimeEnd));
  }
}
