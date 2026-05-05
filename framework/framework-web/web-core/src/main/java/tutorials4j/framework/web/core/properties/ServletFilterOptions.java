package tutorials4j.framework.web.core.properties;

import jakarta.servlet.DispatcherType;
import lombok.Data;

import java.util.EnumSet;

/**
 * Servlet 过滤器的通用配置选项，用于封装过滤器的 URL 模式、顺序、名称及 Dispatcher 类型。
 * <p>该类的实例通常被嵌套在更高级的配置类中（如 {@link WebHttpProperties}），
 * 允许用户通过配置文件自定义特定过滤器的行为而不修改代码。</p>
 *
 * @author Yun Jiao
 * @see jakarta.servlet.DispatcherType
 * @see org.springframework.boot.web.servlet.FilterRegistrationBean
 */
@Data
public class ServletFilterOptions {
    /**
     * 过滤器匹配的 URL 模式，默认为 "/*" 表示拦截所有请求。
     */
    private String[] urlPatterns = new String[]{"/*"};

    /**
     * 过滤器执行顺序，数值越小优先级越高。
     */
    private Integer order = 1;

    /**
     * 过滤器的名称，用于在注册时标识该过滤器。
     */
    private String name = "defaultServletFilter";

    /**
     * 过滤器适用的 Dispatcher 类型集合，默认包含 ERROR, INCLUDE, REQUEST, FORWARD，
     * 但不包含 ASYNC。
     */
    private EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.ERROR
            , DispatcherType.INCLUDE, DispatcherType.REQUEST, DispatcherType.FORWARD);

}
