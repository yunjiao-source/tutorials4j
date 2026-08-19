package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 * {@link ObjectMapper} 的 Bean 创建器，基于默认实例的拷贝创建新的实例。
 *
 * <p>实现 {@link BeanCreator} 接口，通过拷贝传入的 ObjectMapper 为不同使用者 提供相互独立的实例，避免共享同一配置状态。
 *
 * @author Yun Jiao
 */
public class ObjectMapperCreator implements BeanCreator<ObjectMapper> {
  private final ObjectMapper defaultObjectMapper;

  /**
   * 以传入的 ObjectMapper 为基础创建创建器。
   *
   * @param objectMapper 默认的 ObjectMapper 实例（构造时拷贝一份作为基准）
   */
  public ObjectMapperCreator(ObjectMapper objectMapper) {
    defaultObjectMapper = objectMapper.copy();
  }

  /**
   * 获取默认的 ObjectMapper 实例（共享同一基准实例）。
   *
   * @return 默认 ObjectMapper 实例
   */
  @Override
  public ObjectMapper getInstance() {
    return defaultObjectMapper;
  }

  /**
   * 创建 ObjectMapper 的拷贝实例。
   *
   * @return 新的 ObjectMapper 实例
   */
  @Override
  public ObjectMapper newInstance() {
    return defaultObjectMapper.copy();
  }

  /**
   * 返回创建的 Bean 类型。
   *
   * @return {@link ObjectMapper} 的 Class 对象
   */
  @Override
  public Class<ObjectMapper> getBeanClass() {
    return ObjectMapper.class;
  }
}
