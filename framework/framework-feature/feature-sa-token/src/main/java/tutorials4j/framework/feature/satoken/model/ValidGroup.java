package tutorials4j.framework.feature.satoken.model;

import jakarta.validation.groups.Default;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ValidGroup {
  interface AdminGroup extends Default {}

  interface UserGroup {}
}
