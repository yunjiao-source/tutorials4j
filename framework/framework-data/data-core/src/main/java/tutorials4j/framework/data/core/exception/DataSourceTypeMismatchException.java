package tutorials4j.framework.data.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 数据源类型不匹配
 *
 * @author Yun Jiao
 */
public class DataSourceTypeMismatchException extends FrameworkRuntimeException {
  public DataSourceTypeMismatchException(String sourceType, String targetType) {
    super("数据源类型不匹配");
    addContextValue("源数据库类型", sourceType);
    addContextValue("目标数据库类型", sourceType);
  }
}
