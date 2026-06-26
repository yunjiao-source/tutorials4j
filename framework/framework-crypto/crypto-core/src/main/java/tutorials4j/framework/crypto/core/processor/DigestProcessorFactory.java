package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.exception.CryptoErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DigestProcessorFactory {
  public static final DigestProcessorFactory instance = new DigestProcessorFactory();

  protected EnumMap<DigestCategory, DigestProcessor> processors =
      new EnumMap<>(DigestCategory.class);

  public DigestProcessor findProcessor(String categoryName) {
    DigestCategory category = EnumUtils.getEnum(DigestCategory.class, categoryName);
    if (category == null) {
      throw CryptoErrorCode.CRYPTO_DIGEST_CATEGORY_NOT_EXISTS
          .throwed()
          .param("category", categoryName);
    }
    return findProcessor(category);
  }

  public DigestProcessor findProcessor(DigestCategory category) {
    DigestProcessor processor = processors.get(category);
    if (processor == null) {
      throw CryptoErrorCode.CRYPTO_DIGEST_PROCESSOR_NOT_EXISTS
          .throwed()
          .param("category", category);
    }

    return processor;
  }

  public Map<DigestCategory, DigestProcessor> getProcessors() {
    return Collections.unmodifiableMap(processors);
  }

  public void setProcessors(Map<DigestCategory, DigestProcessor> processors) {
    this.processors.putAll(processors);
  }
}
