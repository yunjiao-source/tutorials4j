package tutorials4j.service.uaa.component;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tutorials4j.feature.core.event.local.AuthEvent;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.ip2region.component.Ip2RegionSearcher;
import tutorials4j.toolkit.webmvc.util.HeaderUtils;
import tutorials4j.toolkit.webmvc.util.WebmvcUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
@RequiredArgsConstructor
public class LoginEventCreatorComponent {
  private final Ip2RegionSearcher ip2RegionSearcher;

  public AuthEvent create(UserModel userModel) {
    AuthEvent authEvent = new AuthEvent();
    BeanUtils.copyProperties(userModel, authEvent);
    authEvent.setUserId(userModel.getUserId());

    HttpServletRequest request = WebmvcUtils.getRequest();

    String ip = WebmvcUtils.getClientIp(request);
    authEvent.setIpAddress(ip);

    String region = ip2RegionSearcher.search(ip);
    authEvent.setRegion(region);

    UserAgent userAgent = UserAgentUtil.parse(HeaderUtils.getUserAgent(request));
    if (userAgent != null) {
      authEvent.setPlatForm(userAgent.getPlatform().getName() + " " + userAgent.getOsVersion());
      authEvent.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
    }

    return authEvent;
  }
}
