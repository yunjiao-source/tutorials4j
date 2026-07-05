package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class JacksonUtils implements ObjectMapperWrapper {
  public static final JacksonUtils instance = new JacksonUtils();

  private ObjectMapper objectMapper;

  @Override
  public ObjectMapper get() {
    return getObjectMapper();
  }
}
