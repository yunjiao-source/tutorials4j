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

  public void fillByProfile(UserModel userModel) {
    this.name = userModel.getName();
    this.nickname = userModel.getNickname();
    this.preferred_username = userModel.getUsername();
    this.profile = userModel.getProfile();
    this.picture = userModel.getAvatar();
    this.website = userModel.getWebsite();
    this.gender = userModel.getGender();
    this.birthdate = userModel.getBirthdate();
    this.zoneInfo = userModel.getZoneInfo();
    this.locale = userModel.getLocaleInfo();
    this.updated_at = userModel.getLastModifiedDate();
  }
}
