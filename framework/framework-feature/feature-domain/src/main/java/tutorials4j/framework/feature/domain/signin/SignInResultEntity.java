package tutorials4j.framework.feature.domain.signin;

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
@Table(name = "t4j_sign_in_result")
@EqualsAndHashCode(callSuper = false)
public class SignInResultEntity extends BaseIdEntity {
  private String account;
  private LocalDate signDate;
  private String source;
  private Boolean signedIn;
  private Boolean repeatedSignIn;
  private Long continuousDays;
  private Long monthlySignedDays;
  private Instant createDate;
}
