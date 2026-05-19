package tutorials4j.framework.tenant.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * @author Yun Jiao
 */
public class DataSourceNotFound extends FrameworkRuntimeException {
  public DataSourceNotFound(String tenant) {
    super("未找到租户数据源");
    addContextValue("租户代码", tenant);
  }
}
