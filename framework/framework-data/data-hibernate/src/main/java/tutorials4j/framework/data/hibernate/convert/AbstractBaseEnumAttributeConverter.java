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

  /**
   * 构造转换器并初始化枚举缓存。
   *
   * @param enumClass 待转换的枚举类型，必须实现 {@link BaseEnum}
   */
  protected AbstractBaseEnumAttributeConverter(Class<E> enumClass) {
    this.enumClass = enumClass;
    initCache();
  }

  /** 将枚举的全部常量按 code 注册到 {@link EnumCache}，供转换时快速查找。 */
  private void initCache() {
    E[] enumConstants = enumClass.getEnumConstants();
    if (enumConstants == null) {
      throw new IllegalArgumentException("不是枚举类型:" + enumClass.getSimpleName());
    }
    EnumCache.registerByValue(enumClass, enumConstants, BaseEnum::getCode);
  }

  /**
   * 将枚举实例转换为其 code 值；入参为 {@code null} 时返回 {@code null}。
   *
   * @param attribute 待转换的枚举实例
   * @return 数据库列对应的 code 值
   */
  @Override
  public T convertToDatabaseColumn(E attribute) {
    return attribute == null ? null : attribute.getCode();
  }

  /**
   * 将数据库列中的 code 值还原为对应的枚举实例；入参为 {@code null} 时返回 {@code null}，code 无法匹配到枚举常量时抛出 {@link
   * IllegalArgumentException}。
   *
   * @param dbData 数据库列中存储的 code 值
   * @return 还原后的枚举实例
   * @throws IllegalArgumentException 数据库值对应的枚举代码不存在时抛出
   */
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
