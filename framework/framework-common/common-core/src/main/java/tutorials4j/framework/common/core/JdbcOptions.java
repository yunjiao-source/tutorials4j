package tutorials4j.framework.common.core;

import lombok.Data;

/**
 * JDBC 连接选项，描述数据源所需的驱动、连接地址、用户名与密码。
 *
 * @author Yun Jiao
 */
@Data
public class JdbcOptions {
  /** JDBC 驱动类名 */
  private String driverClassName;

  /** JDBC 连接地址 */
  private String url;

  /** 数据库用户名 */
  private String username;

  /** 数据库密码 */
  private String password;
}
