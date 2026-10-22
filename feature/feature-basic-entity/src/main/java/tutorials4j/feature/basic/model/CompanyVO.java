package tutorials4j.feature.basic.model;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.basic.entity.CompanyEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class CompanyVO extends BasicVO {
  private String organizeId;

  // 公司性质
  private String companyType;

  // 所属行业
  private String industry;

  // 成立时间
  private Instant establishmentDate;

  // 公司电话
  private String phone;

  // 公司传真
  private String fax;

  // 公司主页
  private String website;

  // 公司地址
  private String address;

  // 公司法人
  private String legalRepresentative;

  // 联系电话
  private String contactPhone;

  // 联系手机
  private String contactMobile;

  // 联系邮箱
  private String contactEmail;

  // 开户银行
  private String openingBank;

  // 银行账户
  private String bankAccount;

  // 经营范围
  private String businessScope;

  public static CompanyVO of(CompanyEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    CompanyVO vo = new CompanyVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }
}
