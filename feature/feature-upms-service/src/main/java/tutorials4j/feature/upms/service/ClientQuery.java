package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.ClientSpecification.clientIdEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.clientNameLike;
import static tutorials4j.feature.upms.service.ClientSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.isAutoConfirmEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.isNewRefreshEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.reservedEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.subjectIdEqual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientQuery extends BaseQuery<ClientEntity> {
  private String clientId;
  private String clientName;
  private String subjectId;
  private YesNoEnum isNewRefresh;
  private YesNoEnum isAutoConfirm;
  private YesNoEnum enabled;
  private YesNoEnum reserved;

  @Override
  protected void addSpec() {
    addSpec(clientNameLike(clientName))
        .addSpec(clientIdEqual(clientId))
        .addSpec(subjectIdEqual(subjectId))
        .addSpec(isNewRefreshEqual(isNewRefresh))
        .addSpec(isAutoConfirmEqual(isAutoConfirm))
        .addSpec(enabledEqual(enabled))
        .addSpec(reservedEqual(reserved));
  }
}
