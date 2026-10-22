package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.ClientSpecification.clientIdLike;
import static tutorials4j.feature.upms.service.ClientSpecification.isAutoConfirmEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.isNewRefreshEqual;
import static tutorials4j.feature.upms.service.ClientSpecification.subjectIdLike;

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
  private String subjectId;
  private YesNoEnum isNewRefresh;
  private YesNoEnum isAutoConfirm;

  @Override
  protected void addSpec() {
    addSpec(clientIdLike(clientId))
        .addSpec(subjectIdLike(subjectId))
        .addSpec(isNewRefreshEqual(isNewRefresh))
        .addSpec(isAutoConfirmEqual(isAutoConfirm));
  }
}
