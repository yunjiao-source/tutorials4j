package tutorials4j.framework.data.hibernate.convert;

import jakarta.persistence.AttributeConverter;
import java.util.concurrent.ConcurrentHashMap;
import tutorials4j.framework.common.core.bean.BaseEnum;
import tutorials4j.framework.common.core.support.EnumCache;

/**
 * JPA 属性转换器抽象基类，用于实现 {@link BaseEnum} 枚举与数据库列之间的双向转换。
 *
 * <p>该转换器将枚举的 {@link BaseEnum#getCode()} 值持久化到数据库，并在从数据库读取时根据 code 还原为枚举实例。 内部使用 {@link
 * ConcurrentHashMap} 缓存 code 到枚举的映射，以提高转换性能。
 *
 * <p><b>使用示例：</b>
 *
 * <pre>{@code
 * // 定义一个实现 BaseEnum 的枚举
 * public enum Status implements BaseEnum<Integer> {
 *     ACTIVE(1, "激活"), INACTIVE(0, "未激活");
 *     // 实现 getCode() / getName()
 * }
 *
 * // 创建对应的转换器子类
 * \@Converter(autoApply = true)
 * public class StatusConverter extends AbstractBaseEnumAttributeConverter<Status, Integer> {
 *     public StatusConverter() {
 *         super(Status.class);
 *     }
 * }
 * }</pre>
 *
 * @param <E> 枚举类型，必须同时实现 {@link Enum} 和 {@link BaseEnum}&lt;T&gt;
 * @param <T> 数据库中存储的编码类型，即 {@code BaseEnum} 中 {@code getCode()} 的返回类型
 * @author Yun Jiao
 * @see BaseEnum
 * @see AttributeConverter
 */
public abstract class AbstractBaseEnumAttributeConverter<E extends Enum<E> & BaseEnum<T>, T>
    implements AttributeConverter<E, T> {

  private final Class<E> enumClass;

  protected AbstractBaseEnumAttributeConverter(Class<E> enumClass) {
    this.enumClass = enumClass;
    initCache();
  }

  private void initCache() {
    E[] enumConstants = enumClass.getEnumConstants();
    if (enumConstants == null) {
      throw new IllegalArgumentException("不是枚举类型:" + enumClass.getSimpleName());
    }
    EnumCache.registerByValue(enumClass, enumConstants, BaseEnum::getCode);
  }

  @Override
  public T convertToDatabaseColumn(E attribute) {
    return attribute == null ? null : attribute.getCode();
  }

  @Override
  public E convertToEntityAttribute(T dbData) {
    if (dbData == null) {
      return null;
    }
    E enumValue = EnumCache.findByValue(enumClass, dbData);
    if (enumValue == null) {
      throw new IllegalArgumentException(
          "不存在的枚举代码, enum=" + enumClass.getSimpleName() + ", code=" + dbData);
    }
    return enumValue;
  }
}
