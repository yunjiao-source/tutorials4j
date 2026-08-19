package tutorials4j.framework.common.spring.web;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * Servlet 过滤器的通用配置选项，用于封装过滤器的 URL 模式、顺序、名称及 Dispatcher 类型。
 *
 * <p>该类的实例通常被嵌套在更高级的配置类中，允许用户通过配置文件自定义特定过滤器的行为而不修改代码。
 *
 * @author Yun Jiao
 * @see DispatcherType
 */
@Data
public class ServletFilterOptions {
  /** 是否启用该过滤器，默认 false */
  private boolean enabled = false;

  /** 过滤器匹配的 URL 模式，默认为 "/*" 表示拦截所有请求。 */
  private String[] urlPatterns = new String[] {"/*"};

  /** 过滤器执行顺序，数值越小优先级越高。 */
  private Integer order;

  /** 过滤器的名称，用于在注册时标识该过滤器。 */
  private String name;

  /** 过滤器适用的 Dispatcher 类型集合 */
  private EnumSet<DispatcherType> dispatcherTypes =
      EnumSet.of(
          DispatcherType.ERROR,
          DispatcherType.INCLUDE,
          DispatcherType.REQUEST,
          DispatcherType.FORWARD);

  /**
   * 将当前选项填充到过滤器注册对象中。
   *
   * @param registrationBean 过滤器注册对象
   */
  public void fill(FilterRegistrationBean<?> registrationBean) {
    registrationBean.addUrlPatterns(this.getUrlPatterns());
    if (this.getOrder() != null) {
      registrationBean.setOrder(this.getOrder());
    }
    if (StringUtils.isNotBlank(this.getName())) {
      registrationBean.setName(this.getName());
    }
    registrationBean.setDispatcherTypes(this.getDispatcherTypes());
  }
}
