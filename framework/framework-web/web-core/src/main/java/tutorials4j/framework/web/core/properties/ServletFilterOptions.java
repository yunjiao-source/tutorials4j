package tutorials4j.framework.web.core.properties;

import jakarta.servlet.DispatcherType;
import lombok.Data;

import java.util.EnumSet;

/**
 * Servlet 过滤器选项
 *
 * @author Yun Jiao
 */
@Data
public class ServletFilterOptions {
    /**
     * 匹配url地址
     */
    private String[] urlPatterns = new String[]{"/*"};

    /**
     * 过滤器启动排序，数值越小越先执行
     */
    private Integer order = 1;

    /**
     * 过滤器名称
     */
    private String name = "defaultServletFilter";

    /**
     * 默认不包含：DispatcherType.ASYNC
     */
    private EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.ERROR
            , DispatcherType.INCLUDE, DispatcherType.REQUEST, DispatcherType.FORWARD);

}
