package tutorials4j.feature.upms.model;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.PermissionEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class PermissionVO extends UpmsVO {
  private String code;

  private String name;
  private String resource;
  private String action;
  private String description;

  public static PermissionVO of(PermissionEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    PermissionVO vo = new PermissionVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<PermissionVO> of(Collection<PermissionEntity> entities) {
    Assert.notNull(entities, "entity must not be null");

    return entities.stream().map(PermissionVO::of).toList();
  }
}
