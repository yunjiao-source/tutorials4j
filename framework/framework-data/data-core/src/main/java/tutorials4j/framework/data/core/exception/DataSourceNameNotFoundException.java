package tutorials4j.framework.data.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 数据源名不存在
 *
 * @author Yun Jiao
 */
public class DataSourceNameNotFoundException extends FrameworkRuntimeException {
  public DataSourceNameNotFoundException(String name) {
    super("未找到指定数据源名称");
    addContextValue("数据源名称", name);
  }
}
