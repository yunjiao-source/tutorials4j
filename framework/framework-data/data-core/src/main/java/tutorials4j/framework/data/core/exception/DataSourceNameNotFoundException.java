package tutorials4j.framework.data.core.exception;

/**
 * 数据源名不存在
 *
 * @author Yun Jiao
 */
public class DataSourceNameNotFoundException extends DataFrameworkException {
  public DataSourceNameNotFoundException(String name) {
    super("未找到指定数据源名称");
    addContextValue("数据源名称", name);
  }
}
