package tutorials4j.framework.tenant.core.exception;

/**
 * @author Yun Jiao
 */
public class DataSourceNotFound extends TenantFrameworkException {
  public DataSourceNotFound(String tenant) {
    super("未找到租户数据源");
    addContextValue("租户代码", tenant);
  }
}
