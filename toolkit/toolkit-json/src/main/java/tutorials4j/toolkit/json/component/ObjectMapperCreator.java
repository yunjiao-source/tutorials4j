package tutorials4j.toolkit.json.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tutorials4j.toolkit.core.function.BeanCreator;

/**
 * {@link ObjectMapper} 的 Bean 创建器，基于默认实例的拷贝创建新的实例。
 *
 * <p>实现 {@link BeanCreator} 接口，通过拷贝传入的 ObjectMapper 为不同使用者 提供相互独立的实例，避免共享同一配置状态。
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class ObjectMapperCreator implements BeanCreator<ObjectMapper> {
  private final ObjectMapper objectMapper;
  private final JsonTypeWhiteListLoader jsonTypeWhiteListLoader;

  private ObjectMapper instance;

  /**
   * 获取默认的 ObjectMapper 实例（共享同一基准实例）。
   *
   * @return 默认 ObjectMapper 实例
   */
  @Override
  public ObjectMapper getInstance() {
    if (instance != null) {
      return instance;
    }

    synchronized (this) {
      if (instance != null) {
        return instance;
      }

      PolymorphicTypeValidator typeValidator =
          BasicPolymorphicTypeValidator.builder()
              .allowIfSubType((ctx, clazz) -> jsonTypeWhiteListLoader.contains(clazz.getName()))
              .build();
      instance =
          objectMapper
              .rebuild()
              .activateDefaultTyping(
                  typeValidator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
              .build();
      return instance;
    }
  }

  /**
   * 创建 ObjectMapper 的拷贝实例。
   *
   * @return 新的 ObjectMapper 实例
   */
  @Override
  public ObjectMapper newInstance() {
    return instance.rebuild().build();
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
