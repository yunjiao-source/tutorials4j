package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.exception.CryptoErrorCode;

/**
 * 加密处理器工厂，负责按加密类别查找对应的处理器实例。
 *
 * @author Yun Jiao
 * @see CryptoCategory
 */
public class CryptoProcessorFactory {
  /** 单例实例。 */
  public static final CryptoProcessorFactory instance = new CryptoProcessorFactory();

  /** 加密类别到处理器实例的映射。 */
  protected EnumMap<CryptoCategory, CryptoProcessor> processors =
      new EnumMap<>(CryptoCategory.class);

  /**
   * 根据类别名称查找加密处理器。
   *
   * @param categoryName 加密类别名称
   * @return 对应的加密处理器
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 当类别名称不存在时抛出
   */
  public CryptoProcessor findProcessor(String categoryName) {
    CryptoCategory category = EnumUtils.getEnum(CryptoCategory.class, categoryName);
    if (category == null) {
      throw CryptoErrorCode.CRYPTO_CATEGORY_NOT_EXISTS.throwed().param("category", categoryName);
    }
    return findProcessor(category);
  }

  /**
   * 根据加密类别查找处理器实例。
   *
   * @param category 加密类别
   * @return 对应的加密处理器
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 当该类别未注册处理器时抛出
   */
  public CryptoProcessor findProcessor(CryptoCategory category) {
    CryptoProcessor processor = processors.get(category);
    if (processor == null) {
      throw CryptoErrorCode.CRYPTO_PROCESSOR_NOT_EXISTS.throwed().param("category", category);
    }

    return processor;
  }

  /**
   * 返回当前注册的全部处理器映射（只读视图）。
   *
   * @return 加密类别到处理器的不可修改映射
   */
  public Map<CryptoCategory, CryptoProcessor> getProcessors() {
    return Collections.unmodifiableMap(processors);
  }

  /**
   * 注册一批加密处理器到工厂。
   *
   * @param processors 加密类别到处理器的映射
   */
  public void setProcessors(Map<CryptoCategory, CryptoProcessor> processors) {
    this.processors.putAll(processors);
  }
}
