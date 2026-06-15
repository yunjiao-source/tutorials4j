package tutorials4j.framework.feature.signin.web;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseVO;
import tutorials4j.framework.feature.signin.domain.SignInResultEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SignInResultVO extends BaseVO {
  private String account;
  private LocalDate signDate;
  private String source;
  private Boolean signedIn;
  private Boolean repeatedSignIn;
  private Long continuousDays;
  private Long monthlySignedDays;
  private Instant createDate;

  public static SignInResultVO of(SignInResultEntity entity) {
    SignInResultVO resultVO = new SignInResultVO();
    BeanUtils.copyProperties(entity, resultVO);
    return resultVO;
  }
}
