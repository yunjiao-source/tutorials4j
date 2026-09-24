package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.function.SaOAuth2NotLoginViewFunction;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.feature.oauth.model.LoginView;
import tutorials4j.feature.oauth.service.ClientService;

/**
 * 未登录视图：解析当前授权请求中的 client_id，向登录页展示正在申请授权的应用信息。
 *
 * <p>client_id 缺失或应用不存在时同样渲染登录页，仅不展示应用信息，不影响正常登录流程。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DefaultSaOAuth2NotLoginViewFunction implements SaOAuth2NotLoginViewFunction {
  private final ClientService clientService;

  @Override
  public Object get() {
    String clientId = SaHolder.getRequest().getParam(SaOAuth2Consts.Param.client_id);

    String clientName = null;
    if (StringUtils.isNotBlank(clientId)) {
      clientName =
          clientService
              .getClientRepository()
              .findByClientId(clientId)
              .map(ClientEntity::getClientName)
              .orElse(null);
    }

    var view = LoginView.builder().clientId(clientId).clientName(clientName).build();
    return new ModelAndView("login.html", "view", view);
  }
}
