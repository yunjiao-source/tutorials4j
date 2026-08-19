package tutorials4j.framework.examples.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 人员管理示例控制器，演示 Swagger 文档注解与请求参数校验。
 *
 * @author Yun Jiao
 */
@RestController
@Validated
public class PersonController {
  /** 随机数生成器，用于模拟随机业务失败 */
  private Random ran = new Random();

  /**
   * 创建人员信息，随机模拟业务处理失败场景。
   *
   * @param person 人员信息
   * @return 创建成功的人员信息
   */
  @RequestMapping(path = "/person", method = RequestMethod.POST)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content =
          @Content(
              examples = {
                @ExampleObject(
                    value = INVALID_REQUEST,
                    name = "invalidRequest",
                    description = "Invalid Request"),
                @ExampleObject(
                    value = VALID_REQUEST,
                    name = "validRequest",
                    description = "Valid Request")
              }))
  public Person person(@Valid @RequestBody Person person) {

    int nxt = ran.nextInt(10);
    if (nxt >= 5) {
      throw new RuntimeException("Breaking logic");
    }
    return person;
  }

  /**
   * 根据姓氏查询人员信息，返回硬编码的示例数据。
   *
   * @param lastName 姓氏，必填且长度不超过 10
   * @return 匹配姓氏的人员列表
   */
  @RequestMapping(path = "/personByLastName", method = RequestMethod.GET)
  public List<Person> findByLastName(
      @RequestParam(name = "lastName", required = true) @NotNull @NotBlank @Size(max = 10)
          String lastName) {
    List<Person> hardCoded = new ArrayList<>();
    Person person = new Person();
    person.setAge(20);
    person.setCreditCardNumber("4111111111111111");
    person.setEmail("abc@abc.com");
    person.setEmail1("abc1@abc.com");
    person.setFirstName("Somefirstname");
    person.setLastName(lastName);
    person.setId(1);
    hardCoded.add(person);
    return hardCoded;
  }

  /** 合法的请求示例，用于 Swagger 文档展示 */
  private static final String VALID_REQUEST =
      """
			{
			  "id": 0,
			  "firstName": "string",
			  "lastName": "string",
			  "email": "abc@abc.com",
			  "email1": "abc@abc.com",
			  "age": 20,
			  "creditCardNumber": "4111111111111111",
			  "registrationDate": "20211231"
			}""";

  /** 非法（校验不通过）的请求示例，用于 Swagger 文档展示 */
  private static final String INVALID_REQUEST =
      """
			{
			  "id": 0,
			  "firstName": "string",
			  "lastName": "string",
			  "email": "abcabc.com",
			  "email1": "abcabc.com",
			  "age": 17,
			  "creditCardNumber": "411111111111111",
			  "registrationDate": "string"
			}""";
}
