package tutorials4j.toolkit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * {@link SecurityUtils} 单元测试。
 *
 * @author Yun Jiao
 */
class SecurityUtilsTest {

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("未认证时返回空 Optional 与 null")
  void shouldReturnEmptyWhenNotAuthenticated() {
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    assertThat(SecurityUtils.getAccountOptional()).isEmpty();
    assertThat(SecurityUtils.getAccount()).isNull();
  }

  @Test
  @DisplayName("已认证时返回认证主体名称")
  void shouldReturnAuthenticatedAccountName() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "credentials"));

    assertThat(SecurityUtils.getAccountOptional()).contains("alice");
    assertThat(SecurityUtils.getAccount()).isEqualTo("alice");
  }

  @Test
  @DisplayName("匿名认证时返回匿名用户名")
  void shouldReturnAnonymousAccountName() {
    AnonymousAuthenticationToken authentication =
        new AnonymousAuthenticationToken(
            "anonymous-key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThat(SecurityUtils.getAccount()).isEqualTo("anonymousUser");
  }

  @Test
  @DisplayName("认证主体为对象时返回其字符串表示")
  void shouldUsePrincipalToString() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(new Account("bob"), null));

    assertThat(SecurityUtils.getAccount()).isEqualTo("bob");
  }

  @Test
  @DisplayName("上下文清理后重新返回空值")
  void shouldResetAfterClearingContext() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "credentials"));
    assertThat(SecurityUtils.getAccount()).isEqualTo("alice");

    SecurityContextHolder.clearContext();

    assertThat(SecurityUtils.getAccountOptional()).isEmpty();
    assertThat(SecurityUtils.getAccount()).isNull();
  }

  @Test
  @DisplayName("getAccountOptional 与 getAccount 结果一致")
  void shouldBeConsistentBetweenBothMethods() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "credentials"));

    assertThat(SecurityUtils.getAccountOptional()).contains(SecurityUtils.getAccount());
  }

  record Account(String name) {
    @Override
    public String toString() {
      return name;
    }
  }
}
