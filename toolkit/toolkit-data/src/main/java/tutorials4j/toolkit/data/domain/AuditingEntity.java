package tutorials4j.toolkit.data.domain;

import java.time.Instant;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface AuditingEntity extends Entity {
  String getCreatedBy();

  Instant getCreatedDate();

  String getLastModifiedBy();

  Instant getLastModifiedDate();

  void setCreatedBy(String createdBy);

  void setCreatedDate(Instant createdDate);

  void setLastModifiedBy(String lastModifiedBy);

  void setLastModifiedDate(Instant lastModifiedDate);
}
