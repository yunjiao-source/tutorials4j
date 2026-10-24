package tutorials4j.toolkit.satoken.filter;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;
import tutorials4j.toolkit.webmvc.util.HeaderUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER + 1)
public class SecurityContextServletFilter extends OncePerRequestFilter {
  /** 拦截路由 */
  private List<String> includeList = new ArrayList<>();

  /** 放行路由 */
  private List<String> excludeList = new ArrayList<>();

  public SecurityContextServletFilter addInclude(String... paths) {
    includeList.addAll(Arrays.asList(paths));
    return this;
  }

  public SecurityContextServletFilter addExclude(String... paths) {
    excludeList.addAll(Arrays.asList(paths));
    return this;
  }

  public SecurityContextServletFilter setIncludeList(List<String> pathList) {
    includeList = pathList;
    return this;
  }

  public SecurityContextServletFilter setExcludeList(List<String> pathList) {
    excludeList = pathList;
    return this;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    boolean isContextSet = false;
    try {
      SaRouterStaff saRouterStaff = SaRouter.match(includeList).notMatch(excludeList);
      if (saRouterStaff.isHit) {
        // 从请求头中获取登录id
        String loginId = HeaderUtils.getHeader(request, DefaultConsts.HTTP_HEADER_LOGIN_ID);
        if (StringUtils.isNotBlank(loginId)) {
          isContextSet = setContext(loginId);
        }
      }
      filterChain.doFilter(request, response);
    } finally {
      if (isContextSet) {
        SecurityContextHolder.clearContext();
      }
    }
  }

  private boolean setContext(String loginId) {
    UserModel userModel = SaTokenUtils.getUserFromSession(loginId);
    if (userModel != null) {

      Set<SimpleGrantedAuthority> grantedAuthorities =
          SaTokenUtils.getGrantedAuthorities(loginId).stream()
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toSet());

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(
          new UsernamePasswordAuthenticationToken(
              userModel.getUsername(), userModel.getPassword(), grantedAuthorities));
      SecurityContextHolder.setContext(context);
      return true;
    }

    return false;
  }
}
