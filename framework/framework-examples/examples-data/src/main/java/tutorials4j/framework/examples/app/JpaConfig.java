package tutorials4j.framework.examples.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("jpa")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.jpa"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.jpa"})
public class JpaConfig {
  @PostConstruct
  public void postConstruct() {
    // 1. 构建用户信息（UserDetails 是 Spring Security 标准用户对象）
    UserDetails userDetails =
        User.withUsername("zhangsan")
            .password("123456") // 密码可随意填（手动认证无需校验）
            .roles("USER", "ADMIN") // 角色
            .build();

    // 2. 创建认证对象（核心：未认证/已认证 两种构造器）
    // 参数：用户信息、凭证、权限/角色
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails, // 主体（用户对象）
            null, // 凭证（密码，手动登录设null即可）
            userDetails.getAuthorities() // 权限列表
            );

    // 3. 设置到安全上下文（关键步骤）
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
