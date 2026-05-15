package tutorials4j.framework.examples.swagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ErrorMessage {
  private List<String> errors;

  public ErrorMessage() {}

  public ErrorMessage(List<String> errors) {
    this.errors = errors;
  }

  public ErrorMessage(String error) {
    this(Collections.singletonList(error));
  }

  public ErrorMessage(String... errors) {
    this(Arrays.asList(errors));
  }
}
