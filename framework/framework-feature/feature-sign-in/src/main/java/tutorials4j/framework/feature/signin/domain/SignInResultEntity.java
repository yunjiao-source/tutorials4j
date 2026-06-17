package tutorials4j.framework.feature.signin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.domain.BaseIdEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_sign_in_result")
@EqualsAndHashCode(callSuper = false)
public class SignInResultEntity extends BaseIdEntity {
  @Column(length = 64)
  private String account;

  private LocalDate signDate;

  @Column(length = 64)
  private String source;

  private Boolean signedIn;

  private Boolean repeatedSignIn;

  private Long continuousDays;

  private Long monthlySignedDays;

  private Instant createDate;
}
