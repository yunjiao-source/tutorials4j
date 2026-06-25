package tutorials4j.springboot3.demo1;

import lombok.Data;

@Data
public class UserVO {
  private String name;

  @DataMask(MaskType.PHONE)
  private String phone;

  @DataMask(MaskType.ID_CARD)
  private String idCard;
}
