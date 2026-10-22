package tutorials4j.feature.upms.model;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.toolkit.data.domain.BaseIdVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class AuthHistoryVO extends BaseIdVO<String> {
  private AuthStatus authStatus;
  private Instant createdTime;

  private UserVO user;

  public static AuthHistoryVO of(AuthHistoryEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    AuthHistoryVO vo = new AuthHistoryVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static AuthHistoryVO withUser(AuthHistoryEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    AuthHistoryVO vo = of(entity);
    if (entity.getUser() != null) {
      vo.setUser(UserVO.of(entity.getUser()));
    }
    return vo;
  }
}
