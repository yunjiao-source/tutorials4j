package tutorials4j.framework.common.core;

import lombok.Data;

/**
 * JDBC 连接选项
 *
 * @author Yun Jiao
 */
@Data
public class JdbcOptions {
  private String driverClassName;

  private String url;

  private String username;

  private String password;
}
