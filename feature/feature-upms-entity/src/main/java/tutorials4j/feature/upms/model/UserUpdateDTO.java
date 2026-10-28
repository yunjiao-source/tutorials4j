package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class UserUpdateDTO {

  @NotBlank(message = "名称是必须的")
  private String name;

  @NotBlank(message = "组织是必须的")
  private String organizeId;

  private String nickname;

  private String phoneNumber;

  private String avatar;

  private String email;

  private String profile;

  private String website;

  private GenderEnum gender;

  private LocalDate birthdate;

  private Instant accountExpireAt;

  private Instant credentialsExpireAt;

  private ZoneId zoneInfo;

  private Locale localeInfo;

  private UserStatus status;
}
