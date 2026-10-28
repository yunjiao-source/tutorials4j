package tutorials4j.feature.upms.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.domain.BaseVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class UserVO extends BaseVO<String> {
  private String organizeId;

  private String username;

  private String name;

  private String nickname;

  private String phoneNumber;

  private String avatar;

  private String email;

  private String profile;

  private GenderEnum gender;

  private LocalDate birthdate;

  private Instant accountExpireAt;

  private Instant credentialsExpireAt;

  private ZoneId zoneInfo;

  private Locale localeInfo;

  private UserStatus status;

  private YesNoEnum reserved;

  private OrganizeVO organize;

  public static UserVO of(UserEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    UserVO vo = new UserVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static UserVO withOrganize(UserEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    UserVO vo = of(entity);
    if (entity.getOrganize() != null) {
      vo.setOrganize(OrganizeVO.of(entity.getOrganize()));
    }
    return vo;
  }
}
