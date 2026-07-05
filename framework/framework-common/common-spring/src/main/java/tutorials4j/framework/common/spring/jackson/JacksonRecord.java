package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record JacksonRecord(ObjectMapper objectmapper) implements ObjectMapperWrapper {
  @Override
  public ObjectMapper get() {
    return objectmapper;
  }
}
