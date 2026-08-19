package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

/**
 * 基于单例实例的 Jackson 工具类，持有全局 ObjectMapper 并提供 JSON 序列化与反序列化能力。
 *
 * <p>通过 {@link #instance} 提供全局访问入口，{@code objectMapper} 可由外部注入配置， 并借助 {@link ObjectMapperWrapper}
 * 暴露丰富的转换方法。
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class JacksonUtils implements ObjectMapperWrapper {
  /** JacksonUtils 全局单例实例。 */
  public static final JacksonUtils instance = new JacksonUtils();

  private ObjectMapper objectMapper;

  /**
   * 返回当前持有的 ObjectMapper 实例。
   *
   * @return ObjectMapper 实例
   */
  @Override
  public ObjectMapper get() {
    return getObjectMapper();
  }
}
