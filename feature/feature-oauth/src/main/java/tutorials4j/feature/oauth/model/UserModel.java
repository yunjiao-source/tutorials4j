package tutorials4j.feature.oauth.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class UserModel {
  private String id;

  private String username;

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

  private Instant createDate;

  private Instant lastModifiedDate;
}
