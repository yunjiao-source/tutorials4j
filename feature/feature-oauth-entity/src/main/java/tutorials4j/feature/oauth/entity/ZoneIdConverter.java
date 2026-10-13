package tutorials4j.feature.oauth.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.ZoneId;

/**
 * TODO
 *
 * @author YunJiao Yang
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
