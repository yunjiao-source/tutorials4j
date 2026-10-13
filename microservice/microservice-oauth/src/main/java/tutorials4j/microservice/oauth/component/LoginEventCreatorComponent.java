package tutorials4j.microservice.oauth.component;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.event.UserSessionEvent;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.ip2region.Ip2RegionSearcher;
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

  public UserSessionEvent create(UserModel userModel) {
    UserSessionEvent userSessionEvent = new UserSessionEvent();
    BeanUtils.copyProperties(userModel, userSessionEvent);

    HttpServletRequest request = WebmvcUtils.getRequest();

    String ip = WebmvcUtils.getClientIp(request);
    userSessionEvent.setIpAddress(ip);

    String region = ip2RegionSearcher.search(ip);
    userSessionEvent.setRegion(region);

    UserAgent userAgent = UserAgentUtil.parse(HeaderUtils.getUserAgent(request));
    if (userAgent != null) {
      userSessionEvent.setPlatForm(
          userAgent.getPlatform().getName() + " " + userAgent.getOsVersion());
      userSessionEvent.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
    }

    return userSessionEvent;
  }
}
