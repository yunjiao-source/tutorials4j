package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ObjectMapperCreator implements BeanCreator<ObjectMapper> {
  private final ObjectMapper defaultObjectMapper;

  public ObjectMapperCreator(ObjectMapper objectMapper) {
    defaultObjectMapper = objectMapper.copy();
  }

  @Override
  public ObjectMapper getInstance() {
    return defaultObjectMapper;
  }

  @Override
  public ObjectMapper newInstance() {
    return defaultObjectMapper.copy();
  }

  @Override
  public Class<ObjectMapper> getBeanClass() {
    return ObjectMapper.class;
  }
}
