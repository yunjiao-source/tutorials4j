package tutorials4j.framework.web.core.util;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import tutorials4j.framework.common.core.support.ServletFilterOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WebUtils {

    public static void fill(FilterRegistrationBean<?> registrationBean, ServletFilterOptions options) {
        if (options.getUrlPatterns().length == 0) {
            registrationBean.setEnabled(false);
            return;
        }
        registrationBean.addUrlPatterns(options.getUrlPatterns());
        registrationBean.setOrder(options.getOrder());
        registrationBean.setName(options.getName());
        registrationBean.setDispatcherTypes(options.getDispatcherTypes());
    }
}
