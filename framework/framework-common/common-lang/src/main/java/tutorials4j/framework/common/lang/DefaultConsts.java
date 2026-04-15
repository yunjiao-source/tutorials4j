package tutorials4j.framework.common.lang;

/**
 * 默认常量定义
 *
 * @author Yun Jiao
 */
public interface DefaultConsts {
    /**
     * 时间日期格式
     */
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认租户代码
     */
    String DEFAULT_TENTANT_CODE = "DEFAULT";

    /**
     * http header 名称定义
     */
    String HTTP_HEADER_TENANT = "X-Tenant-Code";
    String HTTP_HEADER_INNER_CALL = "X-Inner-Call";
    String HTTP_HEADER_SESSION_ID = "X-Session-Id";
    String HTTP_HEADER_OPEN_ID = "X-Open-Id";
}
