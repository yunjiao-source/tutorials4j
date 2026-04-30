package tutorials4j.framework.common.core;

import lombok.Data;

/**
 * TODO
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
