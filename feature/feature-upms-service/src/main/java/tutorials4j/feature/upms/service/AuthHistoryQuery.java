package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.AuthHistorySpecification.authStatusIn;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.createdTimeGte;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.createdTimeLte;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.userFullNameLike;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.userIdEqual;
import static tutorials4j.feature.upms.service.AuthHistorySpecification.usernameEqual;

import java.time.Instant;
import java.util.Set;
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
  private String username;
  private String userFullName;
  private Set<AuthStatus> authStatusSet;
  private Instant createdTimeBegin;
  private Instant createdTimeEnd;

  @Override
  protected void addSpec() {
    addSpec(userIdEqual(userId))
        .addSpec(usernameEqual(username))
        .addSpec(userFullNameLike(userFullName))
        .addSpec(authStatusIn(authStatusSet))
        .addSpec(createdTimeGte(createdTimeBegin))
        .addSpec(createdTimeLte(createdTimeEnd));
  }
}
