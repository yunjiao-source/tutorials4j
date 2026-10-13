package tutorials4j.toolkit.webmvc.util;

import static tutorials4j.toolkit.core.constant.DefaultConsts.IP_HEADERS;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface WebmvcUtils {
  static ServletRequestAttributes getRequestAttributes() {
    try {
      RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
      return (ServletRequestAttributes) attributes;
    } catch (Exception e) {
      return null;
    }
  }

  static HttpServletRequest getRequest() {
    try {
      return getRequestAttributes().getRequest();
    } catch (Exception e) {
      return null;
    }
  }

  static String getClientIp(HttpServletRequest request) {
    for (String header : IP_HEADERS) {
      String ip = request.getHeader(header);
      if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
        // 多级代理时，第一个是真实客户端 IP
        if (ip.contains(",")) {
          ip = ip.split(",")[0].trim();
        }
        return ip;
      }
    }
    // 如果以上 header 都没有，则回退到直接连接的 IP
    return request.getRemoteAddr();
  }

  static boolean isPrivateOrReserved(String ip) {
    try {
      InetAddress addr = InetAddress.getByName(ip);
      return addr.isSiteLocalAddress() // 私有地址
          || addr.isLoopbackAddress() // 回环地址
          || addr.isLinkLocalAddress() // 链路本地地址
          || addr.isAnyLocalAddress() // 通配地址
          || addr.isMulticastAddress(); // 组播地址
    } catch (UnknownHostException e) {
      return false;
    }
  }
}
