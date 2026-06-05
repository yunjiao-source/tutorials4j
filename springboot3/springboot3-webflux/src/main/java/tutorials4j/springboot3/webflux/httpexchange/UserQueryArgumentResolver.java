package tutorials4j.springboot3.webflux.httpexchange;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UserQueryArgumentResolver implements HttpServiceArgumentResolver {
  @Override
  public boolean resolve(
      Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
    if (parameter.getParameterType().equals(UserQuery.class)) {
      UserQuery search = (UserQuery) argument;
      if (StringUtils.isNotBlank(search.username())) {
        requestValues.addRequestParameter("username", search.username());
      }

      if (StringUtils.isNotBlank(search.email())) {
        requestValues.addRequestParameter("email", search.email());
      }
      return true;
    }
    return false;
  }
}
