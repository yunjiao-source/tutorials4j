package tutorials4j.feature.oauth.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class UserUpdateModel {

  @NotBlank(message = "名称是必须的")
  private String name;

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
