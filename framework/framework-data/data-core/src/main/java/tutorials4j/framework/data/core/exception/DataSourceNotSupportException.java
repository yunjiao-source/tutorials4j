package tutorials4j.framework.data.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 不支持的数据源
 *
 * @author Yun Jiao
 */
public class DataSourceNotSupportException extends FrameworkRuntimeException {
  public DataSourceNotSupportException(String name) {
    super("不支持的数据源");
    addContextValue("数据源类型", name);
  }
}
