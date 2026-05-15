package tutorials4j.framework.data.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tutorials4j.framework.common.core.support.BaseEnum;
import tutorials4j.framework.data.core.exception.DataFrameworkException;

/**
 * MyBatis 类型处理器，用于实现了 {@link BaseEnum} 接口的枚举类型。
 *
 * <p>该处理器将枚举的 {@code code} 值（存储在数据库中）映射为对应的枚举实例。 内部使用缓存存储编码到枚举常量的映射，以提高性能。
 *
 * <p><b>使用示例：</b>
 *
 * <pre>{@code
 * // 在 MyBatis 配置文件中注册
 * <typeHandlers>
 *     <typeHandler handler="tutorials4j.framework.data.mybatis.BaseEnumTypeHandler"
 *                  javaType="com.example.YourEnum"/>
 * </typeHandlers>
 * }</pre>
 *
 * @param <E> 继承 {@link Enum} 并实现 {@link BaseEnum} 的枚举类型
 * @author Yun Jiao
 */
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum<?>> extends BaseTypeHandler<E> {

  private final Class<E> type;
  private final Map<Object, E> codeToEnumCache = new ConcurrentHashMap<>();

  public BaseEnumTypeHandler(Class<E> type) {
    if (type == null) {
      throw new DataFrameworkException("Type argument cannot be null");
    }
    this.type = type;
    initCache();
  }

  private void initCache() {
    E[] enumConstants = type.getEnumConstants();
    if (enumConstants == null) {
      throw new DataFrameworkException(type.getSimpleName() + " 不是枚举类型");
    }
    for (E e : enumConstants) {
      Object code = e.getCode();
      codeToEnumCache.put(code, e);
    }
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {
    Object code = parameter.getCode();
    switch (code) {
      case String strCode -> ps.setString(i, strCode);
      case Integer intCode -> ps.setInt(i, intCode);
      case Long longCode -> ps.setLong(i, longCode);
      case null, default -> ps.setObject(i, code);
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object code = rs.getObject(columnName);
    return code == null ? null : codeToEnum(code);
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object code = rs.getObject(columnIndex);
    return code == null ? null : codeToEnum(code);
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object code = cs.getObject(columnIndex);
    return code == null ? null : codeToEnum(code);
  }

  private E codeToEnum(Object code) {
    E value = codeToEnumCache.get(code);
    if (value == null) {
      throw new DataFrameworkException("Unknown code: " + code + " for enum " + type.getName());
    }

    return value;
  }
}
