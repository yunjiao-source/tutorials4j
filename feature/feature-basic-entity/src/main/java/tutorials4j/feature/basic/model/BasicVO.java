package tutorials4j.feature.basic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.domain.BaseVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BasicVO extends BaseVO<String> {
  private YesNoEnum reserved;
  private YesNoEnum enabled;
}
