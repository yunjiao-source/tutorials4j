package tutorials4j.framework.common.core.json;

import com.fasterxml.jackson.core.Version;

/**
 * JSON 处理相关的常量定义。
 *
 * @author Yun Jiao
 */
public interface JsonConsts {
    /**
     * 当前 JSON 处理组件所基于的 Jackson 版本。
     */
    Version JSON_VERSION = new Version(2, 21, 2, null, null, null);

    int MODULE_ORDER_DEFAULT = 100;

    int MODULE_ORDER_XSS =  MODULE_ORDER_DEFAULT + 1 ;
}
