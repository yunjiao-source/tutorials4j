package tutorials4j.service.ai.converter;

import java.util.regex.Pattern;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LenientJsonOutputConverter<T> implements StructuredOutputConverter<T> {

  private static final Pattern FENCE = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```");

  private final BeanOutputConverter<T> delegate;

  public LenientJsonOutputConverter(Class<T> targetType) {
    this.delegate = new BeanOutputConverter<>(targetType);
  }

  @Override
  public String getFormat() {
    return delegate.getFormat();
  }

  @Override
  public String getJsonSchema() {
    return delegate.getJsonSchema();
  }

  @Override
  public T convert(String source) {
    var matcher = FENCE.matcher(source);
    String json = matcher.find() ? matcher.group(1).trim() : source.trim();
    return delegate.convert(json);
  }
}
