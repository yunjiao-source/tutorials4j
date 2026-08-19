package tutorials4j.framework.examples.swagger;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.jetbrains.annotations.NotNull;
import tutorials4j.framework.web.validation.DateTimeType;
import tutorials4j.framework.web.validation.LocalDateTimeFormat;

/**
 * 人员信息示例实体，用于演示 Swagger 文档与 Bean Validation 参数校验。
 *
 * @author Yun Jiao
 */
@Data
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {

  /** 人员ID */
  private long id;

  /** 名字，长度至少为 2 */
  @Size(min = 2)
  private String firstName;

  /** 姓氏，不能为空 */
  @NotNull @NotBlank private String lastName;

  /** 邮箱地址，需符合邮箱格式 */
  @Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
  private String email;

  /** 备用邮箱地址 */
  @Email() private String email1;

  /** 年龄，取值 18~30 */
  @Min(18)
  @Max(30)
  private int age;

  /** 信用卡卡号，需通过 Luhn 校验 */
  @CreditCardNumber private String creditCardNumber;

  /** 注册日期，格式为 yyyyMMdd */
  @LocalDateTimeFormat(pattern = "yyyyMMdd", dateTimeType = DateTimeType.Date)
  private String registrationDate;

  /** 无参构造方法。 */
  public Person() {}
}
