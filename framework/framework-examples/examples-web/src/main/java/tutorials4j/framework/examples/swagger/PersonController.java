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
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@Validated
public class PersonController {
  private Random ran = new Random();

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
