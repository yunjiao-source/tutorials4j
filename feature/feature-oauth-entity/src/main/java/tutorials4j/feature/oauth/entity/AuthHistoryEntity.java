package tutorials4j.feature.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.data.hibernate.domain.BaseIdEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "oauth_auth_history",
    indexes = {@Index(name = "oauth_client_idx0", columnList = "user_id")})
public class AuthHistoryEntity extends BaseIdEntity<String> {
  @Column
  @Enumerated(EnumType.STRING)
  private AuthStatus authStatus;

  private Instant createdTime;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
}
