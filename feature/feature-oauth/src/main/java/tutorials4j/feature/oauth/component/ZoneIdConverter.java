package tutorials4j.feature.oauth.component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.ZoneId;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Converter
public class ZoneIdConverter implements AttributeConverter<ZoneId, String> {
  @Override
  public String convertToDatabaseColumn(ZoneId zoneId) {
    return zoneId == null ? null : zoneId.getId(); // 例如 "Europe/Paris"
  }

  @Override
  public ZoneId convertToEntityAttribute(String dbData) {
    return dbData == null ? null : ZoneId.of(dbData);
  }
}
