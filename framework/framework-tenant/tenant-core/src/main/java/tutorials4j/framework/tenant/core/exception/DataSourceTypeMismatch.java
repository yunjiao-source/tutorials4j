package tutorials4j.framework.tenant.core.exception;

/**
 * 数据源类型不匹配
 *
 * @author Yun Jiao
 */
public class DataSourceTypeMismatch extends TenantFrameworkException{
    public DataSourceTypeMismatch(String sourceType, String targetType) {
        super("数据源类型不匹配");
        addContextValue("源数据库类型", sourceType);
        addContextValue("目标数据库类型", sourceType);
    }
}
