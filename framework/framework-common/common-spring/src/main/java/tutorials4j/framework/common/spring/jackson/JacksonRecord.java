package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 基于 record 实现的 {@link ObjectMapperWrapper}，持有并暴露一个 {@link ObjectMapper} 实例。
 *
 * <p>通过 record 组件简洁地封装 ObjectMapper，适用于需要不可变包装的场景。
 *
 * @param objectmapper 持有的 ObjectMapper 实例
 * @author Yun Jiao
 */
public record JacksonRecord(ObjectMapper objectmapper) implements ObjectMapperWrapper {
  /**
   * 返回持有的 ObjectMapper 实例。
   *
   * @return ObjectMapper 实例
   */
  @Override
  public ObjectMapper get() {
    return objectmapper;
  }
}
