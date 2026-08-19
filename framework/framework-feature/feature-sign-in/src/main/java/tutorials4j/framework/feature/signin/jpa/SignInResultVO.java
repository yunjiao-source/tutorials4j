package tutorials4j.framework.feature.signin.jpa;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseVO;

/**
 * 签到结果视图对象，用于接口返回。
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SignInResultVO extends BaseVO {
  /** 签到账号 */
  private String account;

  /** 签到日期 */
  private LocalDate signDate;

  /** 签到来源 */
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

  /**
   * 将签到结果实体转换为视图对象。
   *
   * @param entity 签到结果实体
   * @return 对应的视图对象
   */
  public static SignInResultVO of(SignInResultEntity entity) {
    SignInResultVO resultVO = new SignInResultVO();
    BeanUtils.copyProperties(entity, resultVO);
    return resultVO;
  }
}
