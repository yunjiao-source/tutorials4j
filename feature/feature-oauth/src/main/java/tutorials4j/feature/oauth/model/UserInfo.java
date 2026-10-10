package tutorials4j.feature.oauth.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Data;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.toolkit.core.model.GenderEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class UserInfo {
  private String sub;
  private String name;
  private String nickname;
  private String preferred_username;
  private String profile;
  private String picture;
  private String website;
  private String email;
  private String phoneNumber;
  private GenderEnum gender;
  private LocalDate birthdate;
  private ZoneId zoneInfo;
  private Locale locale;
  private Instant updated_at;

  public void fillByProfile(UserEntity user) {
    this.name = user.getName();
    this.nickname = user.getNickname();
    this.preferred_username = user.getUsername();
    this.profile = user.getProfile();
    this.picture = user.getAvatar();
    this.website = user.getWebsite();
    this.gender = user.getGender();
    this.birthdate = user.getBirthdate();
    this.zoneInfo = user.getZoneInfo();
    this.locale = user.getLocaleInfo();
    this.updated_at = user.getLastModifiedDate();
  }
}
