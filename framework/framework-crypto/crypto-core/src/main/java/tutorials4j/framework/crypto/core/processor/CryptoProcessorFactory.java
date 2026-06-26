package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.exception.CryptoErrorCode;

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
      throw CryptoErrorCode.CRYPTO_CATEGORY_NOT_EXISTS.throwed().param("category", categoryName);
    }
    return findProcessor(category);
  }

  public CryptoProcessor findProcessor(CryptoCategory category) {
    CryptoProcessor processor = processors.get(category);
    if (processor == null) {
      throw CryptoErrorCode.CRYPTO_PROCESSOR_NOT_EXISTS.throwed().param("category", category);
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
