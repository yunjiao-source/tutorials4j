package tutorials4j.framework.data.core.exception;

/**
 * 不支持的数据源
 *
 * @author Yun Jiao
 */
public class DataSourceNotSupportException extends DataFrameworkException {
  public DataSourceNotSupportException(String name) {
    super("不支持的数据源");
    addContextValue("数据源类型", name);
  }
}
