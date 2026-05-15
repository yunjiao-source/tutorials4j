package tutorials4j.springboot3;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置 MyBatis Plus 多租户支持
 *
 * @author Yun Jiao
 */
@Configuration
public class MyBatisPlusConfig {

  @Bean
  public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
    // 添加多租户插件
    TenantLineHandler tenantLineHandler =
        new TenantLineHandler() {
          @Override
          public Expression getTenantId() {
            // 返回当前租户ID
            return new StringValue(TenantContext.getCurrentTenantId());
          }

          @Override
          public boolean ignoreTable(String tableName) {
            // 忽略多租户的表（比如公共表）
            return "common_table".equalsIgnoreCase(tableName);
          }
        };
    return new TenantLineInnerInterceptor(tenantLineHandler);
  }
}
