package tutorials4j.framework.feature.signin.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.domain.BaseIdEntity;

/**
 * 签到结果实体，对应数据库表 {@code feat_sign_in_result}。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_sign_in_result")
@EqualsAndHashCode(callSuper = false)
public class SignInResultEntity extends BaseIdEntity {
  /** 签到账号 */
  @Column(length = 64)
  private String account;

  /** 签到日期 */
  private LocalDate signDate;

  /** 签到来源 */
  @Column(length = 64)
  private String source;

  /** 本次是否签到成功 */
  private Boolean signedIn;

  /** 本次是否为重复签到 */
  private Boolean repeatedSignIn;

  /** 连续签到天数 */
  private Long continuousDays;

  /** 本月累计签到天数 */
  private Long monthlySignedDays;

  /** 创建时间 */
  private Instant createDate;
}
