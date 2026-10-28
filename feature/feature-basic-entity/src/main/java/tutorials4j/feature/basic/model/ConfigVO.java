package tutorials4j.feature.basic.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.basic.entity.ConfigEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class ConfigVO extends BasicVO {
  private String name;
  private String code;
  private String value;
  private String category;
  private String description;

  public static ConfigVO of(ConfigEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    ConfigVO vo = new ConfigVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }
}
