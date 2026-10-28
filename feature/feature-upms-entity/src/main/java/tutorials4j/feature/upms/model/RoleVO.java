package tutorials4j.feature.upms.model;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.RoleEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class RoleVO extends UpmsVO {
  private String code;

  private String name;

  private String description;

  public static RoleVO of(RoleEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    RoleVO vo = new RoleVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<RoleVO> of(Collection<RoleEntity> entities) {
    Assert.notNull(entities, "entity must not be null");

    return entities.stream().map(RoleVO::of).toList();
  }
}
