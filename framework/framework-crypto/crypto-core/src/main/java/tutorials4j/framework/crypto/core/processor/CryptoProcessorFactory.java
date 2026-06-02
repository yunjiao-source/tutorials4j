package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.CryptoCategory;
import tutorials4j.framework.crypto.core.exception.CryptoException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CryptoProcessorFactory {
  public static final CryptoProcessorFactory instance = new CryptoProcessorFactory();

  protected EnumMap<CryptoCategory, CryptoProcessor> processors =
      new EnumMap<>(CryptoCategory.class);

  public CryptoProcessor findProcessor(String categoryName) {
    CryptoCategory category = EnumUtils.getEnum(CryptoCategory.class, categoryName);
    if (category == null) {
      throw new CryptoException("加解密处理器分类代码不存在:" + categoryName);
    }
    return findProcessor(category);
  }

  public CryptoProcessor findProcessor(CryptoCategory category) {
    CryptoProcessor processor = processors.get(category);
    if (processor == null) {
      throw new CryptoException("根据分类查找加解密处理器未找到, 分类是：" + category);
    }

    return processor;
  }

  public Map<CryptoCategory, CryptoProcessor> getProcessors() {
    return Collections.unmodifiableMap(processors);
  }

  public void setProcessors(Map<CryptoCategory, CryptoProcessor> processors) {
    this.processors.putAll(processors);
  }
}
