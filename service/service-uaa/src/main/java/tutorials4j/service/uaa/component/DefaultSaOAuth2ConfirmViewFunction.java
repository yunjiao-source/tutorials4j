package tutorials4j.service.uaa.component;

import cn.dev33.satoken.oauth2.function.SaOAuth2ConfirmViewFunction;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.feature.upms.model.ConfirmView;
import tutorials4j.feature.upms.model.DefaultScopes;
import tutorials4j.feature.upms.service.ClientService;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
@RequiredArgsConstructor
public class DefaultSaOAuth2ConfirmViewFunction implements SaOAuth2ConfirmViewFunction {
  private final ClientService clientService;

  @Override
  public Object apply(String clientId, List<String> scopes) {
    ClientEntity entity = clientService.findByClientId(clientId);
    if (Objects.equals(entity.getEnabled(), YesNoEnum.no)) {
      throw UpmsFeatureErrorCode.CLIENT_DISABLED.throwed().param("clientId", clientId);
    }

    ConfirmView confirmView =
        ConfirmView.builder()
            .clientId(clientId)
            .clientName(entity.getClientName())
            .scopeNames(DefaultScopes.getScopeNames(scopes))
            .build();
    return new ModelAndView("confirm.html", "view", confirmView);
  }
}
