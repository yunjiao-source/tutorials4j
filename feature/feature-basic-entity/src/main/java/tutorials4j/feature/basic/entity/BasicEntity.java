package tutorials4j.feature.basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.exception.DataErrorCode;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public class BasicEntity extends BaseEntity<String> {

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum isReserved;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum enabled;

  @Override
  protected void prePersist() {
    super.prePersist();

    if (isReserved == null) {
      isReserved = YesNoEnum.no;
    }

    if (enabled == null) {
      enabled = YesNoEnum.yes;
    }
  }

  @Override
  protected void preRemove() {
    super.preRemove();

    if (Objects.equals(isReserved, YesNoEnum.yes)) {
      throw DataErrorCode.RESERVED_DATA_CAN_NOT_REMOVE
          .throwed()
          .param("class", this.getClass().getName());
    }
  }
}
