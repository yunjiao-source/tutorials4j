package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.exception.CryptoErrorCode;

/**
 * 摘要处理器工厂，负责按摘要类别查找对应的处理器实例。
 *
 * @author Yun Jiao
 * @see DigestCategory
 */
public class DigestProcessorFactory {
  /** 单例实例。 */
  public static final DigestProcessorFactory instance = new DigestProcessorFactory();

  /** 摘要类别到处理器实例的映射。 */
  protected EnumMap<DigestCategory, DigestProcessor> processors =
      new EnumMap<>(DigestCategory.class);

  /**
   * 根据类别名称查找摘要处理器。
   *
   * @param categoryName 摘要类别名称
   * @return 对应的摘要处理器
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 当类别名称不存在时抛出
   */
  public DigestProcessor findProcessor(String categoryName) {
    DigestCategory category = EnumUtils.getEnum(DigestCategory.class, categoryName);
    if (category == null) {
      throw CryptoErrorCode.CRYPTO_DIGEST_CATEGORY_NOT_EXISTS
          .throwed()
          .param("category", categoryName);
    }
    return findProcessor(category);
  }

  /**
   * 根据摘要类别查找处理器实例。
   *
   * @param category 摘要类别
   * @return 对应的摘要处理器
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 当该类别未注册处理器时抛出
   */
  public DigestProcessor findProcessor(DigestCategory category) {
    DigestProcessor processor = processors.get(category);
    if (processor == null) {
      throw CryptoErrorCode.CRYPTO_DIGEST_PROCESSOR_NOT_EXISTS
          .throwed()
          .param("category", category);
    }

    return processor;
  }

  /**
   * 返回当前注册的全部处理器映射（只读视图）。
   *
   * @return 摘要类别到处理器的不可修改映射
   */
  public Map<DigestCategory, DigestProcessor> getProcessors() {
    return Collections.unmodifiableMap(processors);
  }

  /**
   * 注册一批摘要处理器到工厂。
   *
   * @param processors 摘要类别到处理器的映射
   */
  public void setProcessors(Map<DigestCategory, DigestProcessor> processors) {
    this.processors.putAll(processors);
  }
}
