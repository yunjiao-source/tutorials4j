package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.feature.core.model.AuthStatus;
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
    name = "upms_auth_history",
    indexes = {@Index(name = "upms_client_idx1", columnList = "user_id")})
public class AuthHistoryEntity extends BaseIdEntity<String> {
  @Column
  @Enumerated(EnumType.STRING)
  private AuthStatus authStatus;

  @Column private Instant createdTime;

  @Column(name = "user_id", insertable = false, updatable = false)
  private String userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "upms_auth_history_fk1"))
  private UserEntity user;
}
