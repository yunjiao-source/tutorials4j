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
 * 人员
 *
 * @author Yun Jiao
 */
@Data
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {

  private long id;

  @Size(min = 2)
  private String firstName;

  @NotNull @NotBlank private String lastName;

  @Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
  private String email;

  @Email() private String email1;

  @Min(18)
  @Max(30)
  private int age;

  @CreditCardNumber private String creditCardNumber;

  @LocalDateTimeFormat(pattern = "yyyyMMdd", dateTimeType = DateTimeType.Date)
  private String registrationDate;

  public Person() {}
}
