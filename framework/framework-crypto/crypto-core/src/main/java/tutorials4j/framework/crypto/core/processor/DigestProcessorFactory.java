package tutorials4j.framework.crypto.core.processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.exception.CryptoException;

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
      throw new CryptoException("摘要处理器分类代码不存在:" + categoryName);
    }
    return findProcessor(category);
  }

  public DigestProcessor findProcessor(DigestCategory category) {
    DigestProcessor processor = processors.get(category);
    if (processor == null) {
      throw new CryptoException("根据分类查找摘要处理器未找到, 分类是：" + category);
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
