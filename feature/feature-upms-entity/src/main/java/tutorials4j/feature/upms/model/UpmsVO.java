package tutorials4j.feature.upms.model;

import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.domain.BaseVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class UpmsVO extends BaseVO<String> {
  private YesNoEnum enabled;

  private YesNoEnum reserved;
}
