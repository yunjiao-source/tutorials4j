package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.function.SaOAuth2ConfirmViewFunction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import tutorials4j.feature.oauth.model.ConfirmView;
import tutorials4j.feature.oauth.model.DefaultScopes;
import tutorials4j.feature.oauth.service.ClientService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DefaultSaOAuth2ConfirmViewFunction implements SaOAuth2ConfirmViewFunction {
  private final ClientService clientService;

  @Override
  public Object apply(String clientId, List<String> scopes) {
    var entity = clientService.findByClientId(clientId);

    var confirmView =
        ConfirmView.builder()
            .clientId(clientId)
            .clientName(entity.getClientName())
            .scopeNames(DefaultScopes.getScopeNames(scopes))
            .build();
    return new ModelAndView("confirm.html", "view", confirmView);
  }
}
